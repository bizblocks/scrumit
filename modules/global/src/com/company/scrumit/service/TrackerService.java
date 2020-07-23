package com.company.scrumit.service;

import com.company.scrumit.entity.IncidentStatus;
import com.company.scrumit.entity.Tracker;

import java.util.List;

public interface TrackerService {
    String NAME = "scrumit_TrackerService";

    IncidentStatus updateIncidentStatus(Tracker incident);

    String getEmailFormString(String source);

    List<String> getAllThreadIds();

    Tracker getTrackerByThreadId(String threadId);
}