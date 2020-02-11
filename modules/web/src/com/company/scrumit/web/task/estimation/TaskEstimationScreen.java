package com.company.scrumit.web.task.estimation;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskEstimation;
import com.company.scrumit.service.TaskEstimationService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.ValueLoadContext;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.*;
@UiController("TaskEstimationScreen")
@UiDescriptor("task-estimation-screen.xml")
public class TaskEstimationScreen extends Screen {

    @Inject
    private TaskEstimationService taskEstimationService;

    @Inject
    private Button estimate;

    @Inject
    private Table<Task> table;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private DataManager dataManager;

    @Inject
    private Screens screens;

    @Inject
    private CollectionContainer<Task> taskDc;

    @Inject
    private CollectionLoader<Task> taskDl;


    private User currentUser;
//            = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();;


    @Subscribe
    protected void onInit(InitEvent event) {
        getTaskDc();
    }

    private void getTaskDc() {
        currentUser = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        List<Task> allTasks =  dataManager.loadList(LoadContext.create(Task.class).setQuery(
                LoadContext.createQuery("select e from scrumit$Task e")
        ));
        LoadContext context = LoadContext.create(TaskEstimation.class)
                .setQuery(LoadContext.createQuery(
                        "select e from scrumit$TaskEstimation e "));
        List<TaskEstimation> estimatedTasks = dataManager.loadList(context);
        List<UUID> estimatedtaskIds = new ArrayList<>();
        for (int i = 0; i < estimatedTasks.size(); i++) {
            if(estimatedTasks.get(i).getUserID().toString().equals(currentUser.getId().toString()))
            {
                estimatedtaskIds.add(estimatedTasks.get(i).getTaskId());
            }
        }

        for (int i = 0; i < allTasks.size(); i++) {
            if(!estimatedtaskIds.contains(allTasks.get(i).getId())) {
                taskDc.getMutableItems().add(allTasks.get(i));
            }
        }
    }

    public List<TaskEstimation> getAllTaskEstimationsObjectsFromDb() {
        LoadContext context = LoadContext.create(TaskEstimation.class)
                .setQuery(LoadContext.createQuery(
                        "select e from scrumit$TaskEstimation e "));
        List<TaskEstimation> estimatedTasks = dataManager.loadList(context);
        return estimatedTasks;
    }

    public void onEstimateTaskClick() {

        Task singleSelected = table.getSingleSelected();
        if (singleSelected != null) {
            UUID taskId = singleSelected.getId();
            TaskEstimationEdit screen = screens.create(TaskEstimationEdit.class, OpenMode.DIALOG);
            screen.setTaskEstimationDs(taskId, currentUser.getId());
            screen.show();
        }
    }
}