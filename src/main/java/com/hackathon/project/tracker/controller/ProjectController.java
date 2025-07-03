package com.hackathon.project.tracker.controller;

import com.hackathon.project.tracker.io.ProjectRequest;
import com.hackathon.project.tracker.io.ProjectResponse;
import com.hackathon.project.tracker.service.ProjectServiceImple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectServiceImple projectService;

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody ProjectRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(){
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/get/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable("projectId") String projectId){
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @Transactional
    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable("projectId") String projectId){
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("Project deleted");
    }

    @Transactional
    @PutMapping("/edit/{projectId}")
    public ResponseEntity<ProjectResponse> editProject(@PathVariable String projectId,@RequestBody ProjectRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(projectService.editProject(projectId,request));
    }

    @GetMapping("/filter")
    public List<ProjectResponse> filterProjects(@RequestParam(required = false) String status,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                @RequestParam(required = false) String createdByEmail) {
        return projectService.filterProjects(status, endDate, createdByEmail);
    }



}
