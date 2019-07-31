package com.company.scrumit.web.task.estimation;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskEstimation;
import com.company.scrumit.service.TaskEstimationService;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskEstimationScreen extends AbstractWindow {

    @Inject
    private TaskEstimationService taskEstimationService;

    @Inject
    private Button estimate;

    @Inject
    private Table<Task> table;

    @Inject
    private CollectionDatasource<Task, UUID> tasksDs;

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
        tasksDs.refresh(params);
    }

    public void onEstimateTaskClick() {

        Task singleSelected = table.getSingleSelected();
        if (singleSelected != null) {

            Map<String, Object> params = new HashMap<>();
//            params.put("taskId", taskId);
            openLookup(TaskEstimation.class, this::onEstimationLookup, WindowManager.OpenType.DIALOG, params);
        }
    }

    private void onEstimationLookup(Collection<TaskEstimation> selected) {
        if (selected.size() != 0) {
            TaskEstimation estimation = selected.iterator().next();
            Task task = table.getSingleSelected();
            taskEstimationService.estimateTask(estimation, task);
            tasksDs.refresh();
        }
    }

    public void onUpdateClick() {
        tasksDs.refresh();
    }
}