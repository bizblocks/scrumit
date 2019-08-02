package com.company.scrumit.service;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskEstimation;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(TaskEstimationService.NAME)
public class TaskEstimationServiceBean implements TaskEstimationService {

    @Inject
    private Persistence persistence;

    @Inject
    private DataManager dataManager;

    @Override
    public void estimateTask(TaskEstimation estimation, Task task) {
        try(Transaction t = persistence.createTransaction()) {
            task.setEstimation(estimation);
            EntityManager em = persistence.getEntityManager();
            em.merge(task);
            t.commit();
        }
    }

    @Override
    public List<TaskEstimation> findAllSorted() {
        List<TaskEstimation> estimations = dataManager.load(TaskEstimation.class)
                .query("select estim from scrumit$TaskEstimation estim order by estim.value")
                .list();
        return estimations;
    }

}