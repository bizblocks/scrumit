package com.company.scrumit.listener;

import com.company.scrumit.entity.Task;
import com.company.scrumit.service.TaskService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.listener.AfterCompleteTransactionListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;

@Component("scrumit_TransactionListener")
public class TransactionListener implements AfterCompleteTransactionListener {


    @Inject
    private TaskService taskService;

    @Override
    public void afterComplete(boolean committed, Collection<Entity> detachedEntities) {
        if (committed) {
            detachedEntities.forEach(entity -> {
                if (entity.getClass().equals(Task.class)) {
                    taskService.updateTesting((Task) entity);
                }
            });
        }
    }
}