package com.taskflow.dto.project;

import com.taskflow.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for project data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private Long ownerId;
    private String ownerName;
    private int taskCount;
    private long completedTaskCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
