package com.example.demo.EmailOtp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ✅ Generalized OTP email sender with message
    public void sendOtpEmail(String to, int otp, String subject, String messagePrefix) {
        String message = messagePrefix + otp + "\n\n"
                + "This OTP is valid for 2 minutes. Please do not share it with anyone.\n\n"
                + "If you did not request this, please ignore this email.";
        sendEmail(to, subject, message);
    }

    // ✅ Send OTP for registration
    public void sendEmailOtp(String to, int otp) {
        sendOtpEmail(to, otp, "Email OTP Verification", "Your OTP for verification is: ");
    }

    // ✅ Send OTP for forgot password
    public void sendPasswordResetEmail(String to, int otp) {
        sendOtpEmail(to, otp, "Password Reset OTP", "Your OTP for resetting your password is: ");
    }

    // ✅ Generalized email sender
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(to);
        emailMessage.setSubject(subject);
        emailMessage.setText(text);
        try {
            mailSender.send(emailMessage);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    // ✅ Send Registration Confirmation Email
    public void sendRegistrationConfirmation(String to, String name) {
        String subject = "Blood Donation Registration Successful";
        String message = "Dear " + name + ",\n\n"
                + "Thank you for registering as a blood donor. "
                + "Your willingness to donate blood can save lives.\n\n"
                + "We appreciate your support.\n\n"
                + "Best regards,\nBlood Donation Team";

        sendEmail(to, subject, message);
    }
}
