package com.hackathon.project.tracker.io;

import com.hackathon.project.tracker.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskResponse {
    private String taskId;              // UUID for task (safe to expose)
    private String title;                // Task title
    private String description;         // Task details
    private TaskStatus status;              // Example: PLANNED, IN_PROGRESS, COMPLETED
    private LocalDate endDate;          // Task deadline
    private String assignedToEmail;     // Email of person assigned (avoid full user entity exposure)
    private String projectName;         // Optional: To show project this task belongs to
}
