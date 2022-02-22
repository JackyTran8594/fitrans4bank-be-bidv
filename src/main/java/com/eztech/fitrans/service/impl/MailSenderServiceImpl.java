package com.eztech.fitrans.service.impl;

import java.io.IOException;
import java.util.Map;

import com.eztech.fitrans.service.MailSenderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SimpleMailMessage mailMessage;

    @Override
    public void sendMessage(String to, String subject, String text)  {

        // TODO Auto-generated method stub
        try {
            
        } catch (MessagingException e) {
            //TODO: handle exception
        }
        
    }

    @Override
    public void sendTemplateMessage(String to, String subject, Map<String, Object> templateModel) {
        // TODO Auto-generated method stub
        try {
            
        } catch (MessagingException e) {
            //TODO: handle exception
        }
        
    }

    @Override
    public void sendMessageAttachment(String to, String subject, String pathAttachment, String text) {
        // TODO Auto-generated method stub
        try {
            
        } catch (MessagingException e) {
            //TODO: handle exception
        }
    }

    

    
    


}
