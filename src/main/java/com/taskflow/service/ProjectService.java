package com.taskflow.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.common.PageResponse;
import com.taskflow.dto.project.CreateProjectRequest;
import com.taskflow.dto.project.ProjectResponse;
import com.taskflow.dto.project.UpdateProjectRequest;
import com.taskflow.entity.Project;
import com.taskflow.entity.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.exception.UnauthorizedException;
import com.taskflow.mapper.PageMapper;
import com.taskflow.mapper.ProjectMapper;
import com.taskflow.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final PageMapper pageMapper;

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, User owner) {
        log.info("Creating new project: {} for user: {}", request.getName(), owner.getEmail());

        Project project = projectMapper.toEntity(request);
        project.setOwner(owner);

        projectRepository.save(project);
        log.info("Project created with id: {}", project.getId());

        return projectMapper.toResponse(project);
    }

    /**
     * Get a project by ID.
     * @param id project ID
     * @param userId requesting user ID
     * @return project response
     */
    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id, Long userId) {
        Project project = findProjectById(id);
        validateOwnership(project, userId);
        return projectMapper.toResponse(project);
    }

    /**
     * Get all projects for a user.
     * @param userId user ID
     * @param pageable pagination info
     * @return paginated projects
     */
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> getUserProjects(Long userId, Pageable pageable) {
        Page<Project> projects = projectRepository.findByOwnerId(userId, pageable);
        return pageMapper.toPageResponse(projects, projectMapper::toResponse);
    }

    /**
     * Search projects.
     * @param userId user ID
     * @param search search term
     * @param pageable pagination info
     * @return paginated search results
     */
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> searchProjects(Long userId, String search, Pageable pageable) {
        Page<Project> projects = projectRepository.searchByOwner(userId, search, pageable);
        return pageMapper.toPageResponse(projects, projectMapper::toResponse);
    }

    /**
     * Update a project.
     * @param id project ID
     * @param request update details
     * @param userId requesting user ID
     * @return updated project response
     */
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request, Long userId) {
        log.info("Updating project with id: {}", id);

        Project project = findProjectById(id);
        validateOwnership(project, userId);

        projectMapper.updateEntity(request, project);
        projectRepository.save(project);

        log.info("Project updated: {}", project.getName());
        return projectMapper.toResponse(project);
    }

    /**
     * Delete a project.
     * @param id project ID
     * @param userId requesting user ID
     */
    @Transactional
    public void deleteProject(Long id, Long userId) {
        log.info("Deleting project with id: {}", id);

        Project project = findProjectById(id);
        validateOwnership(project, userId);

        projectRepository.delete(project);
        log.info("Project deleted: {}", project.getName());
    }

    /**
     * Find project by ID (internal use).
     */
    public Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    /**
     * Validate that user owns the project.
     */
    public void validateOwnership(Project project, Long userId) {
        if (!project.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to access this project");
        }
    }
}
