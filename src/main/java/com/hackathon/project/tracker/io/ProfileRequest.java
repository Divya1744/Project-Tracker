package com.hackathon.project.tracker.io;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class ProfileRequest {
    @NotBlank(message = "Name should not be empty")
    private String name;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String confirmPassword;
    @Email(message = "Enter valid Email address")
    @NotNull(message = "Email should not be empty")
    private String email;

}
