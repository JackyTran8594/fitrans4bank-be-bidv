package com.eztech.fitrans.service;

import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;

public interface MailSenderService {
    
    void sendMessage(String to, String subject, String text);
    
    void sendTemplateMessage(String to, String subject, Map<String, Object> templateModel);

    void sendMessageAttachment(String to, String subject, String pathAttachment, String text);
}
