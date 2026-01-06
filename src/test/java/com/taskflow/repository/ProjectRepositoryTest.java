package com.taskflow.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.taskflow.config.JpaConfig;
import com.taskflow.entity.Project;
import com.taskflow.entity.ProjectStatus;
import com.taskflow.entity.User;

/**
 * Integration tests for ProjectRepository.
 * Tests custom query methods and JPA auditing.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
@DisplayName("ProjectRepository Tests")
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .email("owner@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .enabled(true)
                .build();
        entityManager.persist(owner);
    }

    @Test
    @DisplayName("Should find projects by owner ID")
    void shouldFindProjectsByOwnerId() {
        Project project1 = Project.builder()
                .name("Project 1")
                .description("Description 1")
                .owner(owner)
                .build();
        Project project2 = Project.builder()
                .name("Project 2")
                .description("Description 2")
                .owner(owner)
                .build();

        entityManager.persist(project1);
        entityManager.persist(project2);
        entityManager.flush();

        Page<Project> projects = projectRepository.findByOwnerId(owner.getId(), PageRequest.of(0, 10));

        assertThat(projects.getContent()).hasSize(2);
        assertThat(projects.getContent()).extracting(Project::getName)
                .containsExactlyInAnyOrder("Project 1", "Project 2");
    }

    @Test
    @DisplayName("Should find projects by owner ID and status")
    void shouldFindProjectsByOwnerIdAndStatus() {
        Project activeProject = Project.builder()
                .name("Active Project")
                .owner(owner)
                .status(ProjectStatus.ACTIVE)
                .build();
        Project archivedProject = Project.builder()
                .name("Archived Project")
                .owner(owner)
                .status(ProjectStatus.ARCHIVED)
                .build();

        entityManager.persist(activeProject);
        entityManager.persist(archivedProject);
        entityManager.flush();

        List<Project> activeProjects = projectRepository.findByOwnerIdAndStatus(owner.getId(), ProjectStatus.ACTIVE);

        assertThat(activeProjects).hasSize(1);
        assertThat(activeProjects.get(0).getName()).isEqualTo("Active Project");
    }

    @Test
    @DisplayName("Should search projects by name or description")
    void shouldSearchProjectsByNameOrDescription() {
        Project project1 = Project.builder()
                .name("TaskFlow App")
                .description("A task management application")
                .owner(owner)
                .build();
        Project project2 = Project.builder()
                .name("Another Project")
                .description("Contains TaskFlow integration")
                .owner(owner)
                .build();
        Project project3 = Project.builder()
                .name("Unrelated")
                .description("Something else")
                .owner(owner)
                .build();

        entityManager.persist(project1);
        entityManager.persist(project2);
        entityManager.persist(project3);
        entityManager.flush();

        Page<Project> results = projectRepository.searchByOwner(owner.getId(), "TaskFlow", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent()).extracting(Project::getName)
                .containsExactlyInAnyOrder("TaskFlow App", "Another Project");
    }

    @Test
    @DisplayName("Should count projects by owner ID")
    void shouldCountProjectsByOwnerId() {
        entityManager.persist(Project.builder().name("P1").owner(owner).build());
        entityManager.persist(Project.builder().name("P2").owner(owner).build());
        entityManager.persist(Project.builder().name("P3").owner(owner).build());
        entityManager.flush();

        long count = projectRepository.countByOwnerId(owner.getId());

        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should check if project exists by ID and owner ID")
    void shouldCheckProjectExistsByIdAndOwnerId() {
        Project project = Project.builder()
                .name("Test Project")
                .owner(owner)
                .build();
        entityManager.persist(project);
        entityManager.flush();

        boolean exists = projectRepository.existsByIdAndOwnerId(project.getId(), owner.getId());
        boolean notExists = projectRepository.existsByIdAndOwnerId(project.getId(), 999L);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
