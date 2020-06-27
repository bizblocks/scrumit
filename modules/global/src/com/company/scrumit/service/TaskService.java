package com.company.scrumit.service;


import com.company.scrumit.entity.Performer;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Team;
import com.haulmont.cuba.security.entity.User;


import java.util.List;
import java.util.Set;

public interface TaskService {
    String NAME = "scrumit_TaskService";

    Set<Task> tasksByProject(Task project);

    Set<Task> tasksByTeam(Team team);

    List<Task> tasksInWorkNowByUser(Performer user);

    void endWorkForTask(Task currentTask, User user);
}