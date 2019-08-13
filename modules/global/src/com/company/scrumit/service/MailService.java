package com.company.scrumit.service;


import com.company.scrumit.entity.IncomingMessage;

public interface MailService {
    String NAME = "scrumit_MailService";

    IncomingMessage GetLastMessage() throws Exception;
}