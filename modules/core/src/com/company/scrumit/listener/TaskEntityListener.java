package com.company.scrumit.listener;

import com.company.scrumit.entity.Status;
import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import java.sql.Connection;
import com.company.scrumit.entity.Task;

@Component("scrumit_TaskEntityListener")
public class TaskEntityListener implements BeforeUpdateEntityListener<Task> {

    @Override
    public void onBeforeUpdate(Task entity, EntityManager entityManager) {
        Tracker parentTracker = entity.getParentBug();
        int countTask = parentTracker.getTask().size();
        int countControlTask = 0;
        for (Task task: parentTracker.getTask()) {
            if (task.getControl() == true) {
                countControlTask++;
            }
        }
        if (countTask == countControlTask) {
            parentTracker.setStatus(Status.Done);
        }
    }
}