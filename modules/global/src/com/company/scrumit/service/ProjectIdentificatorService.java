package com.company.scrumit.service;

import com.company.scrumit.entity.ProjectIdentificator;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Tracker;

public interface ProjectIdentificatorService {
    String NAME = "scrumit_ProjectIdentificatorService";

    String generateTrackerNumber(Tracker tracker);

    ProjectIdentificator getProjectIdentificatorByProject(Task project);
}