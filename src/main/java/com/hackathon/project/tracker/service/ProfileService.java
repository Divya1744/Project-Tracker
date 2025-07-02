package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.io.ProfileRequest;
import com.hackathon.project.tracker.io.ProfileResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);
    ProfileResponse getProfile(String email);
    void sendResetOTP(String email);
    void resetPassword(String email, String otp, String newPassword);
    void resendVerificationOTP(String email);
    void verifyEmail(String email,String otp);
}
