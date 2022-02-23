package com.eztech.fitrans.config.mail;

import java.util.Properties;

import javax.validation.constraints.Email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailSenderConfiguration {
    
    private String EMAIL = "tonyvu8594@gmail.com";
    private String PASS = "tony8594";
    private String TO = "bachtuvu.dev@gmail.com";

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(this.EMAIL);
        mailSender.setPassword(this.PASS);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocal", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public SimpleMailMessage mailTemplate() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(this.TO);
        message.setFrom(this.EMAIL);
        message.setText("test mail sender");
        return message;
    }
}
