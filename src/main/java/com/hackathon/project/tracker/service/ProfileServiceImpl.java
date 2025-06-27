package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.io.ProfileRequest;
import com.hackathon.project.tracker.io.ProfileResponse;
import com.hackathon.project.tracker.model.UserEntity;
import com.hackathon.project.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {

        if(userRepository.existsByEmail(request.getEmail()))
        {

            throw new ResponseStatusException(HttpStatus.CONFLICT,"email already exist");
        }
        if(!request.getPassword().equals(request.getConfirmPassword()))
        {
            System.out.println("Inside createProfile exception method");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password do not match");
        }
        UserEntity newProfile = convertToUserEntity(request);
        userRepository.save(newProfile);
        return convertToProfileResponse(newProfile);
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();

    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ENGINEER")
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpiredAt(0L)
                .resetOtp(null)
                .build();
    }

    public ProfileResponse getProfile(String email){
        UserEntity profile = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("user not found"));
        return convertToProfileResponse(profile);
    }
}
