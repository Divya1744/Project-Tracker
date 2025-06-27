package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.io.ProfileRequest;
import com.hackathon.project.tracker.io.ProfileResponse;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);
    ProfileResponse getProfile(String email);
}
