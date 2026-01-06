package com.taskflow.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.common.PageResponse;
import com.taskflow.dto.task.CreateTaskRequest;
import com.taskflow.dto.task.TaskResponse;
import com.taskflow.dto.task.UpdateTaskRequest;
import com.taskflow.entity.Project;
import com.taskflow.entity.Task;
import com.taskflow.entity.TaskPriority;
import com.taskflow.entity.TaskStatus;
import com.taskflow.entity.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.exception.UnauthorizedException;
import com.taskflow.mapper.PageMapper;
import com.taskflow.mapper.TaskMapper;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TaskService {

    // TODO: add caching for frequently accessed tasks

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final TaskMapper taskMapper;
    private final PageMapper pageMapper;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, Long userId) {
        log.info("Creating new task: {} in project: {}", request.getTitle(), request.getProjectId());

        Project project = projectService.findProjectById(request.getProjectId());
        projectService.validateOwnership(project, userId);

        Task task = taskMapper.toEntity(request);
        task.setProject(project);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssigneeId()));
            task.setAssignee(assignee);
        }

        taskRepository.save(task);
        log.info("Task created with id: {}", task.getId());

        return taskMapper.toResponse(task);
    }

    /**
     * Get a task by ID.
     * @param id task ID
     * @param userId requesting user ID
     * @return task response
     */
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id, Long userId) {
        Task task = findTaskById(id);
        validateTaskAccess(task, userId);
        return taskMapper.toResponse(task);
    }

    /**
     * Get all tasks for a project.
     * @param projectId project ID
     * @param userId requesting user ID
     * @param pageable pagination info
     * @return paginated tasks
     */
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getProjectTasks(Long projectId, Long userId, Pageable pageable) {
        Project project = projectService.findProjectById(projectId);
        projectService.validateOwnership(project, userId);

        Page<Task> tasks = taskRepository.findByProjectId(projectId, pageable);
        return pageMapper.toPageResponse(tasks, taskMapper::toResponse);
    }

    /**
     * Get all tasks for the current user.
     * @param userId user ID
     * @param pageable pagination info
     * @return paginated tasks
     */
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getUserTasks(Long userId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findAllByProjectOwnerId(userId, pageable);
        return pageMapper.toPageResponse(tasks, taskMapper::toResponse);
    }

    /**
     * Get tasks assigned to a user with optional filters.
     * @param userId user ID
     * @param status optional status filter
     * @param priority optional priority filter
     * @param pageable pagination info
     * @return paginated tasks
     */
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getAssignedTasks(Long userId, TaskStatus status, 
                                                        TaskPriority priority, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAssigneeWithFilters(userId, status, priority, pageable);
        return pageMapper.toPageResponse(tasks, taskMapper::toResponse);
    }

    /**
     * Get overdue tasks for a user.
     * @param userId user ID
     * @return list of overdue tasks
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks(Long userId) {
        List<Task> tasks = taskRepository.findOverdueTasks(userId, LocalDate.now());
        return tasks.stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    /**
     * Search tasks in a project.
     * @param projectId project ID
     * @param search search term
     * @param userId requesting user ID
     * @param pageable pagination info
     * @return paginated search results
     */
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> searchTasks(Long projectId, String search, 
                                                   Long userId, Pageable pageable) {
        Project project = projectService.findProjectById(projectId);
        projectService.validateOwnership(project, userId);

        Page<Task> tasks = taskRepository.searchByProject(projectId, search, pageable);
        return pageMapper.toPageResponse(tasks, taskMapper::toResponse);
    }

    /**
     * Update a task.
     * @param id task ID
     * @param request update details
     * @param userId requesting user ID
     * @return updated task response
     */
    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request, Long userId) {
        log.info("Updating task with id: {}", id);

        Task task = findTaskById(id);
        validateTaskAccess(task, userId);

        taskMapper.updateEntity(request, task);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssigneeId()));
            task.setAssignee(assignee);
        }

        taskRepository.save(task);
        log.info("Task updated: {}", task.getTitle());

        return taskMapper.toResponse(task);
    }

    /**
     * Update task status.
     * @param id task ID
     * @param status new status
     * @param userId requesting user ID
     * @return updated task response
     */
    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatus status, Long userId) {
        log.info("Updating task {} status to: {}", id, status);

        Task task = findTaskById(id);
        validateTaskAccess(task, userId);

        task.setStatus(status);
        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    /**
     * Delete a task.
     * @param id task ID
     * @param userId requesting user ID
     */
    @Transactional
    public void deleteTask(Long id, Long userId) {
        log.info("Deleting task with id: {}", id);

        Task task = findTaskById(id);
        validateTaskAccess(task, userId);

        taskRepository.delete(task);
        log.info("Task deleted: {}", task.getTitle());
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    private void validateTaskAccess(Task task, Long userId) {
        if (!task.getProject().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to access this task");
        }
    }
}
