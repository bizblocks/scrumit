package com.company.scrumit.listener;

import com.company.scrumit.entity.Status;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.springframework.stereotype.Component;

@Component("scrumit_TaskEntityListener")
public class TaskEntityListener implements BeforeUpdateEntityListener<Task> {

    @Override
    public void onBeforeUpdate(Task entity, EntityManager entityManager) {
        if (entity.getControl()) {
            if (entity.getTracker().size()>0) {
                for (Tracker tracker : entity.getTracker()) {
                    tracker.setStatus(Status.Done);
                }
            }
        }
    }
}