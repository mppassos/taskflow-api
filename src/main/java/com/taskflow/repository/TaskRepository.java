package com.taskflow.repository;

import com.taskflow.entity.Task;
import com.taskflow.entity.TaskPriority;
import com.taskflow.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Task entity operations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status IN :statuses")
    List<Task> findByProjectIdAndStatusIn(@Param("projectId") Long projectId, 
                                          @Param("statuses") List<TaskStatus> statuses);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :assigneeId AND t.dueDate < :date AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("assigneeId") Long assigneeId, @Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> searchByProject(@Param("projectId") Long projectId, 
                               @Param("search") String search, 
                               Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :assigneeId AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority)")
    Page<Task> findByAssigneeWithFilters(@Param("assigneeId") Long assigneeId,
                                         @Param("status") TaskStatus status,
                                         @Param("priority") TaskPriority priority,
                                         Pageable pageable);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId")
    long countByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.project.owner.id = :ownerId")
    Page<Task> findAllByProjectOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    boolean existsByIdAndProjectOwnerId(Long id, Long ownerId);
}
