package com.company.scrumit.service;

import com.company.scrumit.entity.Priority;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.springframework.stereotype.Component;

@Component("scrumit_TaskListener")
public class TaskListener implements BeforeInsertEntityListener<Task>, BeforeUpdateEntityListener<Task> {

    @Override
    public void onBeforeInsert(Task entity, EntityManager entityManager) {
        entity.setProirity(Priority.Middle);
        updateLevelAndTop(entity, entityManager);
        entityManager.persist(entity);
    }

    @Override
    public void onBeforeUpdate(Task entity, EntityManager entityManager) {
        updateLevelAndTop(entity, entityManager);
    }

    private void updateLevelAndTop(Task entity, EntityManager entityManager) {
        Task parent = entity.getTask();
        entity.setLevel(parent == null ? 0 : parent.getLevel()==null ? 0 : parent.getLevel()+1);
        entity.setTop(parent == null ? null : parent.getTop()==null ? parent : parent.getTop());
    }
}