package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.io.TaskResponse;
import com.hackathon.project.tracker.model.TaskEntity;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    /*public void sendWelcomeEmailWithOtp(String toEmail, String name, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to our platform - Verify your Email");
        message.setText("Hello " + name + ",\n\nThanks for registering with us!\n\n"
                + "Please verify your email using the following OTP: " + otp
                + "\n\nThis OTP expires in 15 minutes.\n\nRegards,\nIT Freaks");
        mailSender.send(message);
    }*/

    public void sendWelcomeEmailWithOtp(String toEmail, String name, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("👋 Welcome to IT Freaks - Verify Your Email");

            String content = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px; }" +
                    ".container { background-color: #fff; padding: 20px; border-radius: 8px; max-width: 600px; margin: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
                    ".header { text-align: center; margin-bottom: 20px; }" +
                    ".header img { width: 100px; height: auto; }" +
                    ".title { background-color: #007bff; color: #fff; padding: 15px; border-radius: 8px 8px 0 0; text-align: center; font-size: 20px; }" +
                    ".content { padding: 15px; }" +
                    ".otp { background-color: #e9ecef; padding: 10px; font-size: 18px; text-align: center; border-radius: 5px; margin: 20px 0; }" +
                    ".footer { margin-top: 20px; font-size: 12px; color: #666; text-align: center; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +

                    "<div class='header'>" +
                    "<img src='cid:logoImage'>" +  // Reference to inline image
                    "</div>" +

                    "<div class='title'>Welcome to IT Freaks!</div>" +

                    "<div class='content'>" +
                    "<p>Hello <b>" + name + "</b>,</p>" +
                    "<p>Thank you for registering with us. To complete your registration, please verify your email using the OTP below:</p>" +

                    "<div class='otp'>" + otp + "</div>" +

                    "<p>This OTP is valid for <b>15 minutes</b>.</p>" +
                    "<p>If you did not initiate this request, please ignore this email.</p>" +

                    "<p>Regards,<br><b>IT Freaks Team</b></p>" +
                    "</div>" +

                    "<div class='footer'>IT Freaks &copy; 2025</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(content, true);

            // Load logo image from resources
            ClassPathResource logoImage = new ClassPathResource("static/images/TrackPoint.png");
            helper.addInline("logoImage", logoImage);

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send OTP email: " + e.getMessage());
        }
    }




    public void sendResetOtp(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🔑 Password Reset - OTP Inside");

            String content = "<!DOCTYPE html>" +
                    "<html><body style='font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px;'>" +
                    "<div style='background-color: #fff; padding: 20px; border-radius: 8px; max-width: 600px; margin: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #dc3545;'>Password Reset Request</h2>" +
                    "<p>We received a request to reset your account password.</p>" +
                    "<p style='background-color:#e9ecef; padding:10px; font-size:18px; text-align:center; border-radius:5px;'>Your OTP: <b>" + otp + "</b></p>" +
                    "<p>This OTP is valid for <b>15 minutes</b>.</p>" +
                    "<p>If you did not request this, please ignore the email.</p>" +
                    "<p>Regards,<br><b>IT Freaks Team</b></p>" +
                    "</div></body></html>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send Reset OTP: " + e.getMessage());
        }
    }



    public void sendVerificationOtp(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("📧 Email Verification - Complete Your Registration");

            String content = "<!DOCTYPE html>" +
                    "<html><body style='font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px;'>" +
                    "<div style='background-color: #fff; padding: 20px; border-radius: 8px; max-width: 600px; margin: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #007bff;'>Email Verification</h2>" +
                    "<p>Thank you for registering with <b>IT Freaks</b>.</p>" +
                    "<p>To complete your account setup, please verify your email using the OTP below:</p>" +
                    "<p style='background-color:#e9ecef; padding:10px; font-size:18px; text-align:center; border-radius:5px;'>Your OTP: <b>" + otp + "</b></p>" +
                    "<p>This OTP is valid for <b>15 minutes</b>.</p>" +
                    "<p>If you did not sign up, please ignore this email.</p>" +
                    "<p>Regards,<br><b>IT Freaks Team</b></p>" +
                    "</div></body></html>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send verification OTP: " + e.getMessage());
        }
    }


    public void verificationSuccess(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🎉 Email Verified Successfully - Welcome!");

            String content = "<!DOCTYPE html>" +
                    "<html><body style='font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px;'>" +
                    "<div style='background-color: #fff; padding: 20px; border-radius: 8px; max-width: 600px; margin: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #28a745;'>Email Verified Successfully</h2>" +
                    "<p>Congratulations! Your email has been verified.</p>" +
                    "<p>You can now log in to your account and access all our services.</p>" +
                    "<p>Welcome aboard!</p>" +
                    "<p>Regards,<br><b>IT Freaks Team</b></p>" +
                    "</div></body></html>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send verification success email: " + e.getMessage());
        }
    }


    public void taskNotify(TaskResponse taskResponse) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(taskResponse.getAssignedToEmail());
            helper.setSubject("📌 New Task Assigned: " + taskResponse.getTitle());

            String content = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px; }" +
                    ".container { background-color: #fff; padding: 20px; border-radius: 8px; max-width: 600px; margin: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
                    ".header { background-color: #007bff; color: #fff; padding: 15px; border-radius: 8px 8px 0 0; text-align: center; font-size: 18px; }" +
                    ".content { padding: 15px; }" +
                    ".task-detail { margin: 10px 0; }" +
                    ".footer { margin-top: 20px; font-size: 12px; color: #666; text-align: center; }" +
                    "b { color: #333; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div style='text-align:center;'><img src='cid:logoImage' style='width: 120px; margin-bottom: 10px;' /></div>" +

                    "<div class='header'>🔔 You Have a New Task Assignment</div>" +

                    "<div class='content'>" +
                    "<p><b>Task:</b> " + taskResponse.getTitle() + "</p>" +
                    "<p><b>Description:</b> " + taskResponse.getDescription() + "</p>" +
                    "<p><b>Project:</b> " + taskResponse.getProjectName() + "</p>" +
                    "<p><b>Deadline:</b> " + taskResponse.getEndDate() + "</p>" +

                    "<div class='task-detail'>" +
                    "<p style='color: #007bff;'>Please log in to the <b>Project Tracker</b> to view full details and update the task status.</p>" +
                    "</div>" +
                    "</div>" +

                    "<div class='footer'>Project Tracker System &copy; 2025</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(content, true);

            // Load logo from resources
            FileSystemResource logo = new FileSystemResource(new File("src/main/resources/static/images/TrackPoint.png"));
            helper.addInline("logoImage", logo);

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send task notification: " + e.getMessage());
        }
    }

    public void sendEscalationEmail(TaskEntity task, String managerEmail, String engineerEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(new String[]{managerEmail, engineerEmail}); // send to both
            helper.setSubject("⚠️ Escalation Alert: Task Delayed - " + task.getTitle());

            String content = "<!DOCTYPE html>" +
                    "<html><body style='font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px;'>" +
                    "<div style='background-color: #fff; padding: 20px; border-radius: 8px; max-width: 600px; margin: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #dc3545;'>🚨 Task Delay Notification</h2>" +
                    "<p><b>Task:</b> " + task.getTitle() + "</p>" +
                    "<p><b>Project:</b> " + task.getProject().getName() + "</p>" +
                    "<p><b>Status:</b> " + task.getStatus().name() + "</p>" +
                    "<p><b>Planned End Date:</b> " + task.getEndDate() + "</p>" +
                    "<hr>" +
                    "<p style='color: #d9534f;'>This task is overdue. Immediate attention is required.</p>" +
                    "<p>Engineer: <b>" + task.getAssignedToEmail() + "</b></p>" +
                    "<p>Manager: <b>" + managerEmail + "</b></p>" +
                    "<br>" +
                    "<p>Regards,<br><b>IT Freaks Team</b></p>" +
                    "</div></body></html>";

            helper.setText(content, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.out.println("Failed to send escalation email: " + e.getMessage());
        }
    }


}
