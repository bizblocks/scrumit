package com.company.scrumit.service;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.company.scrumit.entity.Tracker;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.entity.WorkflowEntity;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowServiceBean;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;


public class ExtWorkflowServiceBean extends WorkflowServiceBean {
    @Inject
    TrackerService trackerService;

    @Inject
    DataManager dataManager;

    @Override
    public UUID startWorkflow(WorkflowEntity entity, Workflow wf) throws WorkflowException {
        UUID result =  super.startWorkflow(entity, wf);
        updateTracker((UUID) entity.getId(),wf.getEntityName());
        return result;
    }

    private boolean checkIfProject(UUID id, String entity) {
        if ("scrumit$Task".equals(entity)){
            Task task = dataManager.load(LoadContext.create(Task.class).setId(id).setView("task-top"));
            if (TaskType.project.equals(task.getType())) return true;
        }else return false;
        return false;
    }

    @Override
    public void finishTask(WorkflowInstanceTask task, Map<String, String> params, String... performersLogin) throws WorkflowException {
        if (checkIfProject(UUID.fromString(task.getInstance().getEntityId()),task.getInstance().getEntityName())) return;
        super.finishTask(task, params, performersLogin);
        updateTracker(UUID.fromString(task.getInstance().getEntityId()),task.getInstance().getEntityName());
    }

    @Override
    public void finishTask(WorkflowInstanceTask task, String... performersLogin) throws WorkflowException {
        if (checkIfProject(UUID.fromString(task.getInstance().getEntityId()),task.getInstance().getEntityName())) return;
        super.finishTask(task,performersLogin);
        updateTracker(UUID.fromString(task.getInstance().getEntityId()),task.getInstance().getEntityName());

    }
    private void updateTracker(UUID id, String entity){
        if ("scrumit$Task".equals(entity)){
            Tracker tracker = dataManager.load(Tracker.class)
                    .query("select t.parentBug from scrumit$Task t where t.id = :id")
                    .parameter("id", id)
                    .view("tracker-taskWorkflow")
                    .one();
            trackerService.updateIncidentStatus(tracker);
        }
    }
}