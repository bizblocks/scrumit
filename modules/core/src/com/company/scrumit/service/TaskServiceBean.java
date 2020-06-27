package com.company.scrumit.service;

import com.company.scrumit.entity.Performer;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Team;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service(TaskService.NAME)
public class TaskServiceBean implements TaskService {

    @Inject
    private DataManager dataManager;

    @Inject
    private HumanResourcesAccountService hService;

    @Override
    public Set<Task> tasksByProject(Task project) {
        return null;
    }

    @Override
    public Set<Task> tasksByTeam(Team team) {
        return null;
    }

    @Override
    public List<Task> tasksInWorkNowByUser(Performer user){
        return dataManager.load(Task.class)
                .query("select f from scrumit$Task f where f.startWork is not null and f.performer = :user")
                .parameter("user", user)
                .view("task-isInWork")
                .list();
    }
    @Override
    public void endWorkForTask(Task currentTask, User user){
        currentTask = dataManager.reload(currentTask,"task-isInWork");
        Date currentTime = new Date();
        Date startWorkDate = currentTask.getStartWork();
        long difference = currentTime.getTime() - startWorkDate.getTime();
        int mitutes = Math.round(difference / (60 * 1000));
        Integer timeAlreadyInWork = currentTask.getActualTime();
        if (timeAlreadyInWork==null){
            timeAlreadyInWork = 0;
        }
        currentTask.setActualTime(timeAlreadyInWork + mitutes);
        hService.createRecord(user, currentTask, startWorkDate, currentTime);
        currentTask.setStartWork(null);
        dataManager.commit(currentTask);
    }

}