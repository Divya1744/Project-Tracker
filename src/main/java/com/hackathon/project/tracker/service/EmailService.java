package com.hackathon.project.tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmailWithOtp(String toEmail, String name, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to our platform - Verify your Email");
        message.setText("Hello " + name + ",\n\nThanks for registering with us!\n\n"
                + "Please verify your email using the following OTP: " + otp
                + "\n\nThis OTP expires in 15 minutes.\n\nRegards,\nIT Freaks");
        mailSender.send(message);
    }


    public void sendResetOtp(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP - Action Required");
        message.setText(
                "Hello,\n\n" +
                        "We received a request to reset your account password.\n\n" +
                        "Your One-Time Password (OTP) is: " + otp + "\n\n" +
                        "This OTP is valid for 15 minutes.\n" +
                        "If you did not request a password reset, please ignore this email.\n\n" +
                        "Regards,\n" +
                        "IT Freaks Team"
        );
        mailSender.send(message);
    }


    public void sendVerificationOtp(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Email Verification - Complete Your Registration");
        message.setText(
                "Hello,\n\n" +
                        "Thank you for registering with IT Freaks.\n\n" +
                        "To complete your account setup, please verify your email using the OTP below:\n\n" +
                        "Your OTP: " + otp + "\n\n" +
                        "This OTP is valid for 15 minutes.\n" +
                        "If you did not sign up for an account, please ignore this email.\n\n" +
                        "Regards,\n" +
                        "IT Freaks Team"
        );
        mailSender.send(message);
    }


    public void verificationSuccess(String toEmail){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Email Verified Successfully - Welcome Aboard!");
        message.setText(
                "Hello,\n\n" +
                        "Congratulations! Your email has been successfully verified.\n\n" +
                        "You can now log in to your account and access all our services.\n\n" +
                        "Welcome to IT Freaks!\n\n" +
                        "Regards,\n" +
                        "IT Freaks Team"
        );
        mailSender.send(message);
    }

}
