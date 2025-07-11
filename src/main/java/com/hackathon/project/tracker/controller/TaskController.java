package com.hackathon.project.tracker.controller;

import com.hackathon.project.tracker.io.TaskRequest;
import com.hackathon.project.tracker.io.TaskResponse;
import com.hackathon.project.tracker.service.TaskServiceImple;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskServiceImple taskService;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> create(@Valid @PathVariable String projectId, @RequestBody TaskRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(projectId, request));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable String projectId, @CurrentSecurityContext(expression = "authentication?.name")String email){
        return ResponseEntity.ok(taskService.getTasksByProject(projectId,email));
    }

    @Transactional
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@Valid @PathVariable String taskId,@RequestBody TaskRequest request,@CurrentSecurityContext(expression = "authentication?.name")String email){
        return ResponseEntity.ok(taskService.updateTask(taskId, request,email));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId,
                                           @CurrentSecurityContext(expression = "authentication?.name")String email) {
        taskService.deleteTask(taskId,email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @GetMapping("/tasks/filter")
    public ResponseEntity<List<TaskResponse>> filterTasks(@RequestParam(required = false)String projectId,
                                                          @RequestParam(required = false)String status,
                                                          @CurrentSecurityContext(expression = "authentication?.name")String email){
        return ResponseEntity.ok(taskService.filterTasks(projectId, status,email));
    }


}
