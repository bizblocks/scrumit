package com.company.scrumit.service;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskClass;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Service(TaskClassService.NAME)
public class TaskClassServiceBean implements TaskClassService {

    @Inject
    private DataManager dataManager;

    @Override
    public Integer updateAverageHoursDurationForTaskClass(TaskClass taskClass) {
        taskClass = dataManager.reload(taskClass,"taskClass-full");
        List<Task> tasks = dataManager.load(Task.class)
                .query("select f from scrumit$Task f where f.taskClass = :taskClass")
                .parameter("taskClass", taskClass)
                .view("task-tree")
                .list();

        int averageDurationHours = (int) Math.ceil(tasks.stream()
                .filter(task -> task.getRealdurationMins()!=null)
                .collect(Collectors.averagingInt(value -> value.getRealdurationMins())));
        if (taskClass.getAverageDurationHours() == null || taskClass.getAverageDurationHours()!=averageDurationHours){
            taskClass.setAverageDurationHours(averageDurationHours);
            taskClass = dataManager.commit(taskClass);
        }
        return taskClass.getAverageDurationHours();
    }
}