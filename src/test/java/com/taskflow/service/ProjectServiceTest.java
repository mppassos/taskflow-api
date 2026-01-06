package com.taskflow.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.taskflow.dto.common.PageResponse;
import com.taskflow.dto.project.CreateProjectRequest;
import com.taskflow.dto.project.ProjectResponse;
import com.taskflow.dto.project.UpdateProjectRequest;
import com.taskflow.entity.Project;
import com.taskflow.entity.ProjectStatus;
import com.taskflow.entity.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.exception.UnauthorizedException;
import com.taskflow.mapper.PageMapper;
import com.taskflow.mapper.ProjectMapper;
import com.taskflow.repository.ProjectRepository;

/**
 * Unit tests for ProjectService.
 * Tests project management business logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Tests")
@SuppressWarnings({"null", "unchecked"})
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private ProjectService projectService;

    private User owner;
    private Project project;
    private CreateProjectRequest createRequest;
    private ProjectResponse projectResponse;

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
                .description("Test Description")
                .status(ProjectStatus.ACTIVE)
                .owner(owner)
                .build();

        createRequest = CreateProjectRequest.builder()
                .name("Test Project")
                .description("Test Description")
                .build();

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .status(ProjectStatus.ACTIVE)
                .ownerId(1L)
                .ownerName("John Doe")
                .build();
    }

    @Nested
    @DisplayName("Create Project Tests")
    class CreateProjectTests {

        @Test
        @DisplayName("Should create project successfully")
        void shouldCreateProjectSuccessfully() {
            when(projectMapper.toEntity(createRequest)).thenReturn(project);
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse response = projectService.createProject(createRequest, owner);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Test Project");
            assertThat(response.getOwnerId()).isEqualTo(1L);
            
            verify(projectRepository).save(any(Project.class));
        }
    }

    @Nested
    @DisplayName("Get Project Tests")
    class GetProjectTests {

        @Test
        @DisplayName("Should get project by ID")
        void shouldGetProjectById() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse response = projectService.getProject(1L, owner.getId());

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when project not found")
        void shouldThrowExceptionWhenProjectNotFound() {
            when(projectRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> projectService.getProject(1L, owner.getId()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when user is not owner")
        void shouldThrowExceptionWhenUserIsNotOwner() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            assertThatThrownBy(() -> projectService.getProject(1L, 999L))
                    .isInstanceOf(UnauthorizedException.class);
        }
    }

    @Nested
    @DisplayName("Get User Projects Tests")
    class GetUserProjectsTests {

        @Test
        @DisplayName("Should get user projects with pagination")
        void shouldGetUserProjectsWithPagination() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Project> page = new PageImpl<>(List.of(project), pageable, 1);
            PageResponse<ProjectResponse> expectedResponse = PageResponse.<ProjectResponse>builder()
                    .content(List.of(projectResponse))
                    .pageNumber(0)
                    .pageSize(10)
                    .totalElements(1)
                    .totalPages(1)
                    .build();

            when(projectRepository.findByOwnerId(owner.getId(), pageable)).thenReturn(page);
            when(pageMapper.toPageResponse(eq(page), any(Function.class))).thenReturn(expectedResponse);

            // Act
            PageResponse<ProjectResponse> response = projectService.getUserProjects(owner.getId(), pageable);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Update Project Tests")
    class UpdateProjectTests {

        @Test
        @DisplayName("Should update project successfully")
        void shouldUpdateProjectSuccessfully() {
            UpdateProjectRequest updateRequest = UpdateProjectRequest.builder()
                    .name("Updated Project")
                    .build();

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse response = projectService.updateProject(1L, updateRequest, owner.getId());

            assertThat(response).isNotNull();
            verify(projectMapper).updateEntity(updateRequest, project);
            verify(projectRepository).save(project);
        }
    }

    @Nested
    @DisplayName("Delete Project Tests")
    class DeleteProjectTests {

        @Test
        @DisplayName("Should delete project successfully")
        void shouldDeleteProjectSuccessfully() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            projectService.deleteProject(1L, owner.getId());

            verify(projectRepository).delete(project);
        }
    }
}
