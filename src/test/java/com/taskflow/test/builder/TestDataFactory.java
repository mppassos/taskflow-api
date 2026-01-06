package com.taskflow.test.builder;

import java.time.LocalDate;

import com.taskflow.dto.auth.AuthResponse;
import com.taskflow.dto.auth.LoginRequest;
import com.taskflow.dto.auth.RegisterRequest;
import com.taskflow.dto.project.CreateProjectRequest;
import com.taskflow.dto.project.ProjectResponse;
import com.taskflow.dto.task.CreateTaskRequest;
import com.taskflow.dto.task.TaskResponse;
import com.taskflow.entity.Project;
import com.taskflow.entity.ProjectStatus;
import com.taskflow.entity.Task;
import com.taskflow.entity.TaskPriority;
import com.taskflow.entity.TaskStatus;
import com.taskflow.entity.User;

public final class TestDataFactory {

    private TestDataFactory() {}

    // ============== User Builders ==============

    public static User.UserBuilder<?, ?> defaultUser() {
        return User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .enabled(true);
    }

    public static User createUser(Long id, String email) {
        return User.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .password("encodedPassword")
                .enabled(true)
                .build();
    }

    // ============== Project Builders ==============

    public static Project.ProjectBuilder<?, ?> defaultProject(User owner) {
        return Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .status(ProjectStatus.ACTIVE)
                .owner(owner);
    }

    public static Project createProject(Long id, String name, User owner) {
        return Project.builder()
                .id(id)
                .name(name)
                .description("Test Description")
                .status(ProjectStatus.ACTIVE)
                .owner(owner)
                .build();
    }

    public static CreateProjectRequest.CreateProjectRequestBuilder defaultCreateProjectRequest() {
        return CreateProjectRequest.builder()
                .name("Test Project")
                .description("Test Description");
    }

    public static ProjectResponse.ProjectResponseBuilder defaultProjectResponse() {
        return ProjectResponse.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .status(ProjectStatus.ACTIVE)
                .ownerId(1L)
                .ownerName("John Doe")
                .taskCount(0)
                .completedTaskCount(0);
    }

    // ============== Task Builders ==============

    public static Task.TaskBuilder<?, ?> defaultTask(Project project) {
        return Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .project(project);
    }

    public static Task createTask(Long id, String title, Project project) {
        return Task.builder()
                .id(id)
                .title(title)
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .project(project)
                .build();
    }

    public static CreateTaskRequest.CreateTaskRequestBuilder defaultCreateTaskRequest(Long projectId) {
        return CreateTaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .projectId(projectId)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(7));
    }

    public static TaskResponse.TaskResponseBuilder defaultTaskResponse() {
        return TaskResponse.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .projectId(1L)
                .projectName("Test Project")
                .overdue(false);
    }

    // ============== Auth Builders ==============

    public static RegisterRequest.RegisterRequestBuilder defaultRegisterRequest() {
        return RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123");
    }

    public static LoginRequest.LoginRequestBuilder defaultLoginRequest() {
        return LoginRequest.builder()
                .email("john@example.com")
                .password("password123");
    }

    public static AuthResponse.AuthResponseBuilder defaultAuthResponse() {
        return AuthResponse.builder()
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(AuthResponse.UserInfo.builder()
                        .id(1L)
                        .email("john@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .role("USER")
                        .build());
    }
}
