package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.model.TaskEntity;
import com.hackathon.project.tracker.model.TaskStatus;

import com.hackathon.project.tracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EscalationService {

    private final TaskRepository taskRepository;
    private final EmailService emailService;
    public void checkEscalation() {

        System.out.println("checking escalation...");
        List<TaskEntity> tasks = taskRepository.findAll();
        LocalDate today = LocalDate.now();
        for(TaskEntity task : tasks){
            if((task.getEndDate() != null && task.getEndDate().isBefore(today)) &&
                    (!task.getStatus().equals(TaskStatus.COMPLETED))){
                String managerEmail = task.getProject().getCreatedBy().getEmail();
                String engineerEmail = task.getAssignedToEmail();
                emailService.sendEscalationEmail(task,managerEmail,engineerEmail);
            }
        }
    }
}
