package com.company.scrumit.service;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Team;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service(TaskService.NAME)
public class TaskServiceBean implements TaskService {

    @Override
    public Set<Task> tasksByProject(Task project) {
        return null;
    }

    @Override
    public Set<Task> tasksByTeam(Team team) {
        return null;
    }
}