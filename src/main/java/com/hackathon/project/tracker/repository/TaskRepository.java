package com.hackathon.project.tracker.repository;

import com.hackathon.project.tracker.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity,Long> {
    List<TaskEntity> findAllByProject_ProjectId(String projectId);

    Optional<TaskEntity> findByTaskId(String taskId);

    @Query(nativeQuery = true, value =
            "select * from tbl_tasks where (:projectId is null or project_Id = :projectId) and " +
                    "(:status is null or status = :status)")
    List<TaskEntity> filterTasks(@Param("projectId")String projectId, @Param("status")String status);

    @Query(nativeQuery = true,value = "select * from tbl_tasks where (assigned_to_email = :email) and " +
            "(:status is null or status = :status) and (project_id is null or project_id = :projectId)")
    List<TaskEntity> filterTasksEngineers(String email, String status,String projectId);

    boolean existsByAssignedToEmailAndProjectProjectId(String email, String projectId);

    List<TaskEntity>  findAllByProject_ProjectIdAndAssignedToEmail(String projectId, String email);
}
