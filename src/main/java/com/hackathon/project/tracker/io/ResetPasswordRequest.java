package com.hackathon.project.tracker.io;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "otp is required")
    private String otp;
    @NotBlank(message = "password is required")
    private String newPassword;
}
