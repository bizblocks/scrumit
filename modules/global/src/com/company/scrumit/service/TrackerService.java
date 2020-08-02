package com.company.scrumit.service;

import com.company.scrumit.entity.IncidentStatus;
import com.company.scrumit.entity.Message;
import com.company.scrumit.entity.Tracker;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface TrackerService {
    String NAME = "scrumit_TrackerService";

    IncidentStatus updateIncidentStatus(Tracker incident);

    String getEmailFormString(String source);

    List<String> getAllThreadIds();

    Tracker getTrackerByThreadId(String threadId);

    Message sendReplyMessageinTrackerDiscussion(Tracker tracker, String text) throws GeneralSecurityException, MessagingException, IOException;
}