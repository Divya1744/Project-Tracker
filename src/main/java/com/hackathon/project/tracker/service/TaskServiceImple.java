package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.io.TaskRequest;
import com.hackathon.project.tracker.io.TaskResponse;
import com.hackathon.project.tracker.model.*;
import com.hackathon.project.tracker.repository.ProjectRepository;
import com.hackathon.project.tracker.repository.TaskRepository;
import com.hackathon.project.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImple implements TaskService{

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Override
    public TaskResponse createTask(String projectId, TaskRequest request) {
        TaskEntity task = convertToTaskEntity(projectId, request);
        taskRepository.save(task);
        TaskResponse response = convertToTaskResponse(task);
        emailService.taskNotify(response);
        return response;
    }

    private TaskResponse convertToTaskResponse(TaskEntity task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .endDate(task.getEndDate())
                .assignedToEmail(task.getAssignedToEmail())
                .projectName(task.getProject().getName())
                .build();
    }

    private TaskEntity convertToTaskEntity(String projectId, TaskRequest request) {
        ProjectEntity project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project Not Found"));
        return TaskEntity.builder()
                .taskId(UUID.randomUUID().toString())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.PLANNED)
                .assignedToEmail(request.getAssignedToEmail())
                .endDate(request.getEndDate())
                .project(project)
                .proofUrl(null)
                .build();
    }

    @Override
    public TaskResponse getTaskById(String taskId) {
        TaskEntity task = taskRepository.findByTaskId(taskId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found"));
        return convertToTaskResponse(task);
    }

    @Override
    public List<TaskResponse> getTasksByProject(String projectId,String email) {

        ProjectEntity project = projectRepository.findByProjectId(projectId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Project not found"));
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found"));
        Role role = user.getRole();

        List<TaskEntity> tasks = new ArrayList<>();
        if(Role.ENGINEER.equals(role)){
            if(!taskRepository.existsByAssignedToEmailAndProjectProjectId(email,projectId)){
                throw new  ResponseStatusException(HttpStatus.FORBIDDEN,"You are not assigned any tasks in this project");
            }
             tasks = taskRepository.findAllByProject_ProjectIdAndAssignedToEmail(projectId, email);
        }else if(Role.MANAGER.equals(role)){
            if(!project.getCreatedBy().getId().equals(user.getId())){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied: You didn't create this project");
            }
            tasks = taskRepository.findAllByProject_ProjectId(projectId);
        }else if(Role.ADMIN.equals(role)){
            tasks = taskRepository.findAllByProject_ProjectId(projectId);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid role");
        }

        List<TaskResponse> responses = new ArrayList<>();
        for(TaskEntity task : tasks){
            responses.add(convertToTaskResponse(task));
        }
        return responses;
    }

    @Override
    public TaskResponse updateTask(String taskId, TaskRequest request,String email) {
        TaskEntity task = taskRepository.findByTaskId(taskId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found"));
        ProjectEntity project = task.getProject();
        String oldUser = task.getAssignedToEmail();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found"));
        Role role = user.getRole();
        if(Role.ADMIN.equals(role)) {
            // Admin can update freely
        }else if(Role.MANAGER.equals(role)){
            if (!project.getCreatedBy().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You didn't create this project");
            }
        }else if(Role.ENGINEER.equals(role)){
            if (!task.getAssignedToEmail().equals(email)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Task not assigned to you");
            }
            task.setStatus(request.getStatus());
            return convertToTaskResponse(taskRepository.save(task));
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setEndDate(request.getEndDate());
        task.setAssignedToEmail(request.getAssignedToEmail());
        task.setStatus(request.getStatus());
        taskRepository.save(task);
        TaskResponse response = convertToTaskResponse(task);
        if(!oldUser.equals(task.getAssignedToEmail())){
            emailService.taskNotify(response);
        }
        return response;
    }

    @Override
    public void deleteTask(String taskId,String email) {
        TaskEntity task = taskRepository.findByTaskId(taskId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found"));
        ProjectEntity project = task.getProject();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found"));
        Role role = user.getRole();
        if(Role.MANAGER.equals(role)){
            if(!user.getEmail().equals(email)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not allowed to delete this task.Not created by you");
            }
        }else if(Role.ADMIN.equals(role)) {
            // Admin allowed to delete any task
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete tasks");
        }
        taskRepository.delete(task);
    }

    @Override
    public List<TaskResponse> filterTasks(String projectId, String status,String email) {
        List<TaskEntity> tasks = new ArrayList<>();
        List<TaskResponse> responses = new ArrayList<>();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        Role role = user.getRole();

        if(Role.ADMIN.equals(role)){
            tasks = taskRepository.filterTasks(projectId,status);
        }else if(Role.MANAGER.equals(role)){
            ProjectEntity project = projectRepository.findByProjectId(projectId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"project not found"));
            if(!project.getCreatedBy().getId().equals(user.getId())){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not allowed to view this project's tasks");
            }
            tasks = taskRepository.filterTasks(projectId,status);
        }else if(Role.ENGINEER.equals(role)){
            tasks = taskRepository.filterTasksEngineers(email,status,projectId);
        }

        for(TaskEntity task : tasks){
            responses.add(convertToTaskResponse(task));
        }
        return responses;
    }
}
