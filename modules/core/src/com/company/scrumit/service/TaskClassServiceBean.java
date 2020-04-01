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
        List<Task> tasks = dataManager.load(Task.class)
                .query("select f from scrumit$Task f where f.taskClass = :taskClass")
                .parameter("taskClass", taskClass)
                .view("taskClass-view")
                .list();

        int averageDurationHours = (int) Math.ceil(tasks.stream()
                .filter(task -> task.getRealdurationHours()!=null)
                .collect(Collectors.averagingInt(value -> value.getRealdurationHours())));
        if (taskClass.getAverageDurationHours()!=averageDurationHours){
            taskClass.setAverageDurationHours(averageDurationHours);
            taskClass = dataManager.commit(taskClass);
        }
        return taskClass.getAverageDurationHours();
    }
}