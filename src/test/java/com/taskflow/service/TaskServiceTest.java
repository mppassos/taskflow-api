package com.taskflow.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taskflow.dto.task.CreateTaskRequest;
import com.taskflow.dto.task.TaskResponse;
import com.taskflow.entity.Project;
import com.taskflow.entity.Task;
import com.taskflow.entity.TaskPriority;
import com.taskflow.entity.TaskStatus;
import com.taskflow.entity.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.mapper.PageMapper;
import com.taskflow.mapper.TaskMapper;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;

/**
 * Unit tests for TaskService.
 * Tests task management business logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
@SuppressWarnings("null")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private TaskService taskService;

    private User owner;
    private Project project;
    private Task task;
    private CreateTaskRequest createRequest;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .email("owner@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .owner(owner)
                .build();

        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .project(project)
                .build();

        createRequest = CreateTaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .projectId(1L)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        taskResponse = TaskResponse.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .projectId(1L)
                .projectName("Test Project")
                .build();
    }

    @Nested
    @DisplayName("Create Task Tests")
    class CreateTaskTests {

        @Test
        @DisplayName("Should create task successfully")
        void shouldCreateTaskSuccessfully() {
            when(projectService.findProjectById(1L)).thenReturn(project);
            doNothing().when(projectService).validateOwnership(project, owner.getId());
            when(taskMapper.toEntity(createRequest)).thenReturn(task);
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            when(taskMapper.toResponse(task)).thenReturn(taskResponse);

            TaskResponse response = taskService.createTask(createRequest, owner.getId());

            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("Test Task");
            assertThat(response.getProjectId()).isEqualTo(1L);
            
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("Should create task with assignee")
        void shouldCreateTaskWithAssignee() {
            User assignee = User.builder().id(2L).build();
            createRequest.setAssigneeId(2L);

            when(projectService.findProjectById(1L)).thenReturn(project);
            doNothing().when(projectService).validateOwnership(project, owner.getId());
            when(taskMapper.toEntity(createRequest)).thenReturn(task);
            when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            when(taskMapper.toResponse(task)).thenReturn(taskResponse);

            TaskResponse response = taskService.createTask(createRequest, owner.getId());

            assertThat(response).isNotNull();
            verify(userRepository).findById(2L);
        }
    }

    @Nested
    @DisplayName("Get Task Tests")
    class GetTaskTests {

        @Test
        @DisplayName("Should get task by ID")
        void shouldGetTaskById() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(taskMapper.toResponse(task)).thenReturn(taskResponse);

            TaskResponse response = taskService.getTask(1L, owner.getId());

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when task not found")
        void shouldThrowExceptionWhenTaskNotFound() {
            when(taskRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.getTask(1L, owner.getId()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Update Task Status Tests")
    class UpdateTaskStatusTests {

        @Test
        @DisplayName("Should update task status successfully")
        void shouldUpdateTaskStatusSuccessfully() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            when(taskMapper.toResponse(task)).thenReturn(taskResponse);

            TaskResponse response = taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS, owner.getId());

            assertThat(response).isNotNull();
            verify(taskRepository).save(task);
        }
    }

    @Nested
    @DisplayName("Delete Task Tests")
    class DeleteTaskTests {

        @Test
        @DisplayName("Should delete task successfully")
        void shouldDeleteTaskSuccessfully() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            taskService.deleteTask(1L, owner.getId());

            verify(taskRepository).delete(task);
        }
    }
}
