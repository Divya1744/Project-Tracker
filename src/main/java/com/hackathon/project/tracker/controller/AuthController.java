package com.hackathon.project.tracker.controller;

import com.hackathon.project.tracker.io.AuthRequest;
import com.hackathon.project.tracker.io.ResetPasswordRequest;
import com.hackathon.project.tracker.model.UserEntity;
import com.hackathon.project.tracker.repository.UserRepository;
import com.hackathon.project.tracker.security.JwtUtil;
import com.hackathon.project.tracker.service.CustomUserDetailsService;
import com.hackathon.project.tracker.service.ProfileServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final ProfileServiceImpl profileService;

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){

        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        if(!user.getIsAccountVerified()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please verify your email before logging in");
        }
        try{
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        }
        catch(BadCredentialsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }catch(DisabledException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
        }
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token",token,"email",request.getEmail()));

    }

    @GetMapping("/isAuthenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(email!=null);
    }

    @PostMapping("/send-reset-otp")
    public void resetOtp(@RequestParam String email){
            profileService.sendResetOTP(email);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request){
            profileService.resetPassword(request.getEmail(),request.getOtp(),request.getNewPassword());
    }

    @PostMapping("/resend-verify-otp")
    public void sendVerificationOtp(@RequestBody Map<String, String> request){
            profileService.resendVerificationOTP(request.get("email"));
    }

    @PostMapping("/verify-email")
    public void veriyEmail(@RequestBody Map<String,String> request){
            profileService.verifyEmail(request.get("email"),request.get("otp"));
    }






}
