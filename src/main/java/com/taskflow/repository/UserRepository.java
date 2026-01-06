package com.taskflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taskflow.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.projects WHERE u.id = :id")
    Optional<User> findByIdWithProjects(Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.assignedTasks WHERE u.id = :id")
    Optional<User> findByIdWithTasks(Long id);
}
