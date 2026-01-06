package com.taskflow.repository;

import com.taskflow.entity.Project;
import com.taskflow.entity.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Project entity operations.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByOwnerId(Long ownerId, Pageable pageable);

    List<Project> findByOwnerIdAndStatus(Long ownerId, ProjectStatus status);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :id")
    Optional<Project> findByIdWithTasks(@Param("id") Long id);

    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> searchByOwner(@Param("ownerId") Long ownerId, 
                                @Param("search") String search, 
                                Pageable pageable);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.owner.id = :ownerId")
    long countByOwnerId(@Param("ownerId") Long ownerId);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
