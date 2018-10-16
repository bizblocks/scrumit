package com.company.scrumit.service;


import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Team;

import java.util.Set;

public interface TaskService {
    String NAME = "scrumit_TaskService";

    Set<Task> tasksByProject(Task project);
    Set<Task> tasksByTeam(Team team);

    void updateTesting(Task entity);
}