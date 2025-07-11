package com.hackathon.project.tracker.io;

import com.hackathon.project.tracker.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Task Description is required")
    private String description;
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    @NotBlank(message = "Task must be assigned to someone")
    private String assignedToEmail;
    private String projectId;  // From frontend route param
    private TaskStatus status;
}

