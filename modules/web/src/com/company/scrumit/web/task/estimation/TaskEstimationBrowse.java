package com.company.scrumit.web.task.estimation;

import com.company.scrumit.entity.Task;
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
    private CollectionDatasource<Task, UUID> taskEstimationsDs;

    @Inject
    private UserSessionSource userSessionSource;

    private User currentUser;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        currentUser = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        refreshTasks();
    }

    private void refreshTasks() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getId());
        taskEstimationsDs.refresh(params);
    }
}