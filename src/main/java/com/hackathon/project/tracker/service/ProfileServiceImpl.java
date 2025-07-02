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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"email already exist");
        }
        if(!request.getPassword().equals(request.getConfirmPassword()))
        {
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
                .role("ROLE_ENGINEER")
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpiredAt(0L)
                .resetOtp(null)
                .build();
    }

    public ProfileResponse getProfile(String email){
        UserEntity profile = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return convertToProfileResponse(profile);
    }


    public String generateOtp(){
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        return otp;
    }

    public Long generateOtpExpiry(){
        //generate expiry time in milliseconds for 15 mins
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);
        return expiryTime;
    }

    public void sendWelcomeWithOtp(String email,String name){
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String otp = generateOtp();
        Long expiryTime = generateOtpExpiry();
        user.setVerifyOtp(otp);
        user.setVerifyOtpExpiredAt(expiryTime);
        userRepository.save(user);
        try{
            emailService.sendWelcomeEmailWithOtp(user.getEmail(),name,otp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unable to send Welcome email");
        }

    }

    @Override
    public void sendResetOTP(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String otp = generateOtp();
        long expiryTime = generateOtpExpiry();
        user.setResetOtp(otp);
        user.setResetOtpExpireAt(expiryTime);
        userRepository.save(user);
        try{
            emailService.sendResetOtp(user.getEmail(), otp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unable to send reset password email");
        }
    }

    public void resetPassword(@RequestBody String email, String otp, String newPassword){
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if(!user.getResetOtp().equals(otp) || user.getResetOtp()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid OTP");
        }
        if(user.getResetOtpExpireAt()<System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"OTP Expired");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setResetOtpExpireAt(0L);
        userRepository.save(user);
    }

    @Override
    public void resendVerificationOTP(String email) {

        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        //generate otp
        String otp = generateOtp();
        //generate expiry time in milliseconds for 15 mins
        long expiryTime = generateOtpExpiry();
        user.setVerifyOtp(otp);
        user.setVerifyOtpExpiredAt(expiryTime);
        userRepository.save(user);
        try{
            emailService.sendVerificationOtp(user.getEmail(), otp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unable to send Account verification OTP email");
        }
    }

    public void verifyEmail(@RequestBody String email,String otp){
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getIsAccountVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already verified");
        }
        if (user.getVerifyOtp() == null || !user.getVerifyOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        if (user.getVerifyOtpExpiredAt() < System.currentTimeMillis()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP Expired");
        }
        user.setIsAccountVerified(true);
        user.setVerifyOtp(null);
        user.setVerifyOtpExpiredAt(0L);
        userRepository.save(user);
        emailService.verificationSuccess(email);
    }


}
