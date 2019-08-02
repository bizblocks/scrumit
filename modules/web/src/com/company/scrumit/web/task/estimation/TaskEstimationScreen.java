package com.company.scrumit.web.task.estimation;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskEstimation;
import com.company.scrumit.service.TaskEstimationService;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.*;

public class TaskEstimationScreen extends AbstractWindow {

    @Inject
    private TaskEstimationService taskEstimationService;

    @Inject
    private Table<Task> table;

    @Inject
    private CollectionDatasource<Task, UUID> tasksDs;

    @Inject
    private UserSessionSource userSessionSource;

    private User currentUser;

    @Inject
    private ButtonsPanel buttonsPanelScales;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        currentUser = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        refreshTasks();
        fillScaleButtonsPanel();
    }

    private void fillScaleButtonsPanel() {
        List<TaskEstimation> allSorted = taskEstimationService.findAllSorted();
        allSorted.forEach(this::addEstimationToPanel);
    }

    private void addEstimationToPanel(TaskEstimation estimation) {

        Button button = componentsFactory.createComponent(Button.class);
        button.setCaption(estimation.getDescription());
        button.setAction(new BaseAction("setEstimation") {
            @Override
            public void actionPerform(Component component) {
                Task selectedTask = table.getSingleSelected();
                if (selectedTask != null) {
                    taskEstimationService.estimateTask(estimation, selectedTask);
                    tasksDs.refresh();
                }
            }
        });

        buttonsPanelScales.add(button);
    }

    private void refreshTasks() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getId());
        tasksDs.refresh(params);
    }

    public void onUpdateClick() {
        tasksDs.refresh();
    }
}