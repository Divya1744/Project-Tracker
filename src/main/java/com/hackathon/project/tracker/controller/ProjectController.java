package com.hackathon.project.tracker.controller;

import com.hackathon.project.tracker.io.ProjectRequest;
import com.hackathon.project.tracker.io.ProjectResponse;
import com.hackathon.project.tracker.model.UserEntity;
import com.hackathon.project.tracker.service.ProjectServiceImple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectServiceImple projectService;

    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody ProjectRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(){
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/get/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable("projectId") String projectId, @CurrentSecurityContext(expression = "authentication?.name")String email){

        return ResponseEntity.ok(projectService.getProjectById(projectId,email));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @Transactional
    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable("projectId") String projectId,@CurrentSecurityContext(expression = "authentication?.name") String email){
        projectService.deleteProject(projectId,email);
        return ResponseEntity.ok("Project deleted");
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @Transactional
    @PutMapping("/edit/{projectId}")
    public ResponseEntity<ProjectResponse> editProject(@PathVariable String projectId,@RequestBody ProjectRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(projectService.editProject(projectId,request));
    }

    @GetMapping("/filter")
    public List<ProjectResponse> filterProjects(@RequestParam(required = false) String status,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                @CurrentSecurityContext(expression = "authentication?.name")String email) {
        return projectService.filterProjects(status, endDate, email);
    }



}
