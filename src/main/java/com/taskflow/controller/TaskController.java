package com.taskflow.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.dto.common.PageResponse;
import com.taskflow.dto.task.CreateTaskRequest;
import com.taskflow.dto.task.TaskResponse;
import com.taskflow.dto.task.UpdateTaskRequest;
import com.taskflow.entity.TaskPriority;
import com.taskflow.entity.TaskStatus;
import com.taskflow.entity.User;
import com.taskflow.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management APIs")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task", description = "Creates a new task in a project")
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task", description = "Returns a task by its ID")
    public ResponseEntity<TaskResponse> getTask(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        TaskResponse response = taskService.getTask(id, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Returns all tasks for the authenticated user")
    public ResponseEntity<PageResponse<TaskResponse>> getUserTasks(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<TaskResponse> response = taskService.getUserTasks(user.getId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get project tasks", description = "Returns all tasks in a specific project")
    public ResponseEntity<PageResponse<TaskResponse>> getProjectTasks(
            @AuthenticationPrincipal User user,
            @PathVariable Long projectId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<TaskResponse> response = taskService.getProjectTasks(projectId, user.getId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}/search")
    @Operation(summary = "Search tasks in project", description = "Searches tasks by title or description within a project")
    public ResponseEntity<PageResponse<TaskResponse>> searchTasks(
            @AuthenticationPrincipal User user,
            @PathVariable Long projectId,
            @Parameter(description = "Search term") @RequestParam String q,
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<TaskResponse> response = taskService.searchTasks(projectId, q, user.getId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assigned")
    @Operation(summary = "Get assigned tasks", description = "Returns tasks assigned to the authenticated user")
    public ResponseEntity<PageResponse<TaskResponse>> getAssignedTasks(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Filter by status") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Filter by priority") @RequestParam(required = false) TaskPriority priority,
            @PageableDefault(size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<TaskResponse> response = taskService.getAssignedTasks(user.getId(), status, priority, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks", description = "Returns all overdue tasks for the authenticated user")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(@AuthenticationPrincipal User user) {
        List<TaskResponse> response = taskService.getOverdueTasks(user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates a task's details")
    public ResponseEntity<TaskResponse> updateTask(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse response = taskService.updateTask(id, request, user.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Updates only the task's status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam TaskStatus status) {
        TaskResponse response = taskService.updateTaskStatus(id, status, user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a task")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        taskService.deleteTask(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
