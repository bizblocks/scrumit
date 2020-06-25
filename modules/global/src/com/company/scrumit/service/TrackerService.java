package com.company.scrumit.service;

import com.company.scrumit.entity.IncidentStatus;
import com.company.scrumit.entity.Tracker;

public interface TrackerService {
    String NAME = "scrumit_TrackerService";

    IncidentStatus updateIncidentStatus(Tracker incident);

    String getEmailFormString(String source);
}