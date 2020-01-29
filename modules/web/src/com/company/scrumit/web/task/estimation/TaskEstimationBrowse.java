package com.company.scrumit.web.task.estimation;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskEstimation;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskEstimationBrowse extends AbstractLookup {
    @Inject
    private CollectionDatasource<TaskEstimation, UUID> taskEstimationsDs;

    @Inject
    private UserSessionSource userSessionSource;

    private User currentUser;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        currentUser = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        TaskEstimation taskEstimation = new TaskEstimation();
        taskEstimation.setTaskId( params.get("taskId").toString());
        taskEstimation.setDescription((String) params.get("desc"));
        taskEstimationsDs.addItem(taskEstimation);
    }
}