package com.company.scrumit.web.task.estimation;

import com.company.scrumit.entity.TaskEstimation;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@UiController("TaskEstimationEdit")
@UiDescriptor("task-estimation-edit.xml")
@EditedEntityContainer("taskEstimationDs")
@DialogMode(forceDialog = true, width = "600px", height = "400")
public class TaskEstimationEdit extends Screen {
    @Inject
    private Label<String> taskId;

    @Inject
    private Label<String> userId;

    @Inject
    private TextField<String> value;

    @Inject
    private DataManager dataManager;

    public void setTaskEstimationDs(UUID taskId, UUID userId) {
        LoadContext context = LoadContext.create(TaskEstimation.class)
                .setQuery(LoadContext.createQuery(
                        "select e from scrumit$TaskEstimation e"));
        List<TaskEstimation> estimatedTasks = dataManager.loadList(context);
        estimatedTasks = estimatedTasks.stream().filter(t->t.getUserID().equals(userId)).collect(Collectors.toList());
        List<UUID> taskIds = estimatedTasks.stream().map(TaskEstimation::getTaskId).collect(Collectors.toList());
        if (taskIds.contains(taskId)) {
            this.value.setValue("Already Estimated!");
            this.value.setEditable(false);
        } else {
            TaskEstimation taskEstimation = new TaskEstimation();
            taskEstimation.setTaskId(taskId);
            this.taskId.setValue(taskId.toString());
            taskEstimation.setUserID(userId);
            this.userId.setValue(userId.toString());
        }
    }

    @Subscribe("saveBtn")
    protected  void onSaveBtnClick(Button.ClickEvent event) {
        TaskEstimation taskEstimation = dataManager.create(TaskEstimation.class);
        taskEstimation.setUserID(UUID.fromString(userId.getValue()));
        taskEstimation.setTaskId(UUID.fromString(taskId.getValue()));
        taskEstimation.setValue(Double.valueOf(value.getValue()));
        CommitContext commitContext = new CommitContext(taskEstimation);
        dataManager.commit(commitContext);
        closeWithDefaultAction();
    }

    @Subscribe("closeBtn")
    protected void onCloseBtnClick(Button.ClickEvent event) {
        closeWithDefaultAction();
    }
}