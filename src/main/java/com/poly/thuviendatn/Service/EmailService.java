package com.poly.thuviendatn.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("trantrongdi.genshin@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage(), e);
        }
    }

    public void sendLoginNotification(String to, String username) {
        String subject = "Thông Báo Đăng Nhập - Thư Viện Điện Tử";
        // Định dạng ngày giờ theo pattern yyyy-MM-dd HH:mm:ss
        String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String text = String.format(
                "Chào %s,\n\n" +
                "Tài khoản của bạn vừa đăng nhập vào Thư Viện Điện Tử FPT Polytechnic vào lúc %s.\n" +
                "Nếu đây không phải bạn, vui lòng liên hệ quản trị viên.\n\n" +
                "Trân trọng,\n" +
                "FPT Polytechnic Library",
                username, formattedDateTime
        );
        sendSimpleEmail(to, subject, text);
    }
}