package com.hackathon.project.tracker.repository;

import com.hackathon.project.tracker.model.ProjectEntity;
import com.hackathon.project.tracker.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity,Long> {

    boolean existsByNameIgnoreCaseAndCreatedBy(String projectName, UserEntity user);
    Optional<ProjectEntity> findByProjectId(String projectId);
    void deleteByProjectId(String projectId);
    boolean existsByProjectId(String projectId);
    @Query(nativeQuery = true,
            value = "SELECT p.* FROM tbl_projects p " +
                    "JOIN tbl_users u ON p.created_by = u.id " +
                    "WHERE (:status IS NULL OR p.status = :status) " +
                    "AND (:endDate IS NULL OR p.end_date <= :endDate) " +
                    "AND (:createdByEmail IS NULL OR u.email = :createdByEmail)" )
    List<ProjectEntity> filterProjects(@Param("status")String status,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("createdByEmail")String createdByEmail);
}


