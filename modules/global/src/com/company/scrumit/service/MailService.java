package com.company.scrumit.service;


import com.company.scrumit.entity.*;

import java.util.List;

public interface MailService {
    String NAME = "scrumit_MailService";

    enum ServerType{
        IMAP
    }

    void CreateNewIncomingMessages(ServerType serverType) throws Exception;

    void CreateNewTrackerFromMail(IncomingMessage incomingMessage, String shortDescription, Task task, TrackerPriorityType priority, TicketsType ticketsType);
}