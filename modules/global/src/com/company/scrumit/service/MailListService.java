package com.company.scrumit.service;


import java.io.Serializable;
import java.util.Map;

public interface MailListService {
    String NAME = "scrumit_MailListService";

    void checkEmails();

    void sendEmail(String to, String subject, String templatePath, Map<String, Serializable> params);
}