package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.io.ProjectRequest;
import com.hackathon.project.tracker.io.ProjectResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(ProjectRequest request);
    List<ProjectResponse> getAllProjects();
    ProjectResponse getProjectById(String projectId,String email);
    ProjectResponse editProject(String projectId,ProjectRequest request);
    void deleteProject(String projectId,String email);

}
