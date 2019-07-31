package com.company.scrumit.service;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskEstimation;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(TaskEstimationService.NAME)
public class TaskEstimationServiceBean implements TaskEstimationService {

    @Inject
    private Persistence persistence;

    @Override
    public void estimateTask(TaskEstimation estimation, Task task) {
        try(Transaction t = persistence.createTransaction()) {
            task.setEstimation(estimation);
            EntityManager em = persistence.getEntityManager();
            em.merge(task);
            t.commit();
        }
    }
}