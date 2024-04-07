package com.gachon.learningmate.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 이메일 발송
    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = createSimpleMailMessage(to, subject, text);
        mailSender.send(message);
    }

    // 발신 이메일 정보 설정
    private SimpleMailMessage createSimpleMailMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);            // 보내는 이메일
        message.setSubject(subject);  // 제목
        message.setText(text);        // 내용

        return message;
    }
}
