package com.hackathon.project.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_tasks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String taskId; // UUID
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status; // Example: PLANNED, IN_PROGRESS, COMPLETED
    @Email
    private String assignedToEmail;
    private LocalDate endDate;
    private String proofUrl; // Optional - Link to uploaded proof (image/PDF)
    @ManyToOne
    @JoinColumn(name = "project_id",referencedColumnName = "projectId", nullable = false)
    private ProjectEntity project; // Link to corresponding project
    @CreationTimestamp
    @Column(updatable = false)
    private java.sql.Timestamp createdAt;
    @UpdateTimestamp
    private java.sql.Timestamp updatedAt;
}

