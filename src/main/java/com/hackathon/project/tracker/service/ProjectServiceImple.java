package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.io.ProjectRequest;
import com.hackathon.project.tracker.io.ProjectResponse;
import com.hackathon.project.tracker.model.ProjectEntity;
import com.hackathon.project.tracker.model.Role;
import com.hackathon.project.tracker.model.UserEntity;
import com.hackathon.project.tracker.repository.ProjectRepository;
import com.hackathon.project.tracker.repository.TaskRepository;
import com.hackathon.project.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImple implements ProjectService{

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found: "+email));
        boolean projectExists = projectRepository.existsByNameIgnoreCaseAndCreatedBy(request.getName(), user);
        if(projectExists){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Project with this name already exists for this user.");
        }
        ProjectEntity project = convertToProjectEntity(request,user);
        projectRepository.save(project);
        return convertEntityToResponse(project);

    }

    private ProjectResponse convertEntityToResponse(ProjectEntity project) {
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .createdByEmail(project.getCreatedBy().getEmail())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .build();
    }

    private ProjectEntity convertToProjectEntity(ProjectRequest request,UserEntity user) {
        return ProjectEntity.builder()
                .projectId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status("PLANNED")
                .createdBy(user)
                .build();
    }

    @Override
    public List<ProjectResponse> getAllProjects() {
        List<ProjectEntity> list = projectRepository.findAll();
        List<ProjectResponse> responseList = new ArrayList<>();
        for(ProjectEntity project : list){
            responseList.add(convertEntityToResponse(project));
        }
        return responseList;
    }

    @Override
    public ProjectResponse getProjectById(String projectId,String email) {
        ProjectEntity project = projectRepository.findByProjectId(projectId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Project Not found"));
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        Role role = user.getRole();
        if(Role.ENGINEER.equals(role)){
            boolean isAssigned = isEngineerAssignedToProject(email,projectId);
            if(!isAssigned){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied: You are not assigned to this project");
            }
        }
        else if (Role.MANAGER.equals(role)) {
            if (!project.getCreatedBy().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You did not create this project");
            }
        }

        return convertEntityToResponse(project);
    }

    private boolean isEngineerAssignedToProject(String email, String projectId) {
        return taskRepository.existsByAssignedToEmailAndProjectProjectId(email,projectId);
    }

    @Override
    public ProjectResponse editProject(String projectId,ProjectRequest request) {
        ProjectEntity project = projectRepository.findByProjectId(projectId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"project not found"));
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        projectRepository.save(project);
        return convertEntityToResponse(project);
    }

    @Override
    public void deleteProject(String projectId, String email) {

        ProjectEntity project = projectRepository.findByProjectId(projectId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Project not found"));
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found"));
        Role role = user.getRole();
        if(Role.ENGINEER.equals(role)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not allowed to delete projects");
        }
        else if(Role.MANAGER.equals(role)){
            if(!project.getCreatedBy().getId().equals(user.getId())){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not allowed to delete this project,you did not create this");
            }
        }
        projectRepository.deleteByProjectId(projectId);
    }

    public List<ProjectResponse> filterProjects(String status, LocalDate endDate, String email){
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        Role role = user.getRole();

        List<ProjectEntity> projects = new ArrayList<>();

        if(Role.ADMIN.equals(role)){
                // Admin sees everything
                projects = projectRepository.filterProjects(status, endDate, null);

        }else if (Role.MANAGER.equals(user.getRole())) {
            // Manager sees their own projects
            projects = projectRepository.filterProjects(status, endDate, email);
        }else if (Role.ENGINEER.equals(user.getRole())) {
            // Engineer sees only projects they have tasks in
            projects = projectRepository.filterProjectForEngineers(email, status, endDate);
        }else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown role access denied");
        }

        List<ProjectResponse> responses = new ArrayList<>();
        for(ProjectEntity x : projects){
            responses.add(convertEntityToResponse(x));
        }
        return responses;
    }
}
