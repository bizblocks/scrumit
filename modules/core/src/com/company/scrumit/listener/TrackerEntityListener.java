package com.company.scrumit.listener;

import com.company.scrumit.entity.Tracker;
import com.company.scrumit.service.ProjectIdentificatorService;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("scrumit_TrackerEntityListener")
public class TrackerEntityListener implements BeforeInsertEntityListener<Tracker> {

    @Inject
    private ProjectIdentificatorService projectIdentificatorService;

    @Override
    public void onBeforeInsert(Tracker entity, EntityManager entityManager) {
        String number = projectIdentificatorService.generateTrackerNumber(entity);
        entity.setNumber(number);
        entityManager.persist(entity);
    }


}