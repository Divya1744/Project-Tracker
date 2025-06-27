package com.hackathon.project.tracker.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class AuthRequest {
    private String email;
    private String password;
}
