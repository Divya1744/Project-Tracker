package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.io.TaskRequest;
import com.hackathon.project.tracker.io.TaskResponse;

import java.util.List;

public interface TaskService {

    // Create a new task under a project
    TaskResponse createTask(String projectId, TaskRequest request);

    // Get task details by taskId
    TaskResponse getTaskById(String taskId);

    // Get all tasks under a specific project
    List<TaskResponse> getTasksByProject(String projectId,String email);

    // Update task details
    TaskResponse updateTask(String taskId, TaskRequest request,String email);

    // Delete a task
    void deleteTask(String taskId,String email);

    // Optional: Filter tasks by status (Example)
    List<TaskResponse> filterTasks(String projectId, String status, String email);


}

