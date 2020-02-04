package com.company.scrumit.web.task.estimation;

import com.company.scrumit.entity.TaskEstimation;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.UUID;

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
        TaskEstimation taskEstimation = new TaskEstimation();
        taskEstimation.setTaskId(taskId);
        this.taskId.setValue(taskId.toString());
        taskEstimation.setUserID(userId);
        this.userId.setValue(userId.toString());
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