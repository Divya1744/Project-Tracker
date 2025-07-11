package com.hackathon.project.tracker.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponse {
    private String projectId;
    private String name;
    private String description;
    private String status;
    private String createdByEmail;
    private LocalDate startDate;
    private LocalDate endDate;
}
