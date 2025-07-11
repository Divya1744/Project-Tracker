package com.hackathon.project.tracker.controller;

import com.hackathon.project.tracker.service.EscalationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EscalationController {

    private final EscalationService escalationService;

    @Scheduled(cron = "*/30 * * * * *")
    @GetMapping("/delayTest")
    public void checkEscalation(){
        escalationService.checkEscalation();
    }
}
