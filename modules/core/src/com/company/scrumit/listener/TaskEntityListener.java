package com.company.scrumit.listener;

import com.company.scrumit.entity.Priority;
import com.company.scrumit.entity.Status;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.springframework.stereotype.Component;

@Component("scrumit_TaskEntityListener")
public class TaskEntityListener implements BeforeUpdateEntityListener<Task>, BeforeInsertEntityListener<Task> {

    @Override
    public void onBeforeInsert(Task entity, EntityManager entityManager) {
        entity.setPriority(Priority.Middle);
        updateLevelAndTop(entity, entityManager);
        entityManager.persist(entity);
    }

    @Override
    public void onBeforeUpdate(Task entity, EntityManager entityManager) {
        Tracker parentTracker = entity.getParentBug();
        int countTask = parentTracker.getTask().size();
        int countControlTask = 0;
        for (Task task: parentTracker.getTask()) {
            if (task.getControl() != null) {
                countControlTask++;
            }
        }
        if (countTask == countControlTask) {
            parentTracker.setStatus(Status.Done);
        }
        updateLevelAndTop(entity, entityManager);
    }

    private void updateLevelAndTop(Task entity, EntityManager entityManager) {
        Task parent = entity.getTask();
        entity.setLevel(parent == null ? 0 : parent.getLevel()==null ? 0 : parent.getLevel()+1);
        entity.setTop(parent == null ? null : parent.getTop()==null ? parent : parent.getTop());
    }
}