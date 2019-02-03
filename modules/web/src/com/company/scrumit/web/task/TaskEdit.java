package com.company.scrumit.web.task;

import com.company.scrumit.entity.Task;

import com.company.scrumit.entity.Tracker;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.DataManager;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.Datasource;


import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

public class TaskEdit extends AbstractEditor<Task> {
    private static final long ONEDAY = 24 * 60 * 60 * 1000;


    @Named("fieldGroup.deadline")
    private DateField deadlineField;
    @Named("fieldGroup.begin")
    private DateField beginField;
    @Named("fieldGroup.duration")
    private TextField durationField;
    @Inject
    private Button btnControl;

    @Inject
    private Button btnReady;
    @Inject
    private WorkflowService workflowService;
    @Inject
    private DataManager dataManager;

    @Inject
    private Datasource<Task> taskDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        durationField.addValueChangeListener(this::calcDates);
        beginField.addValueChangeListener(this::calcDates);
        deadlineField.addValueChangeListener(e -> {
            if (beginField.getValue() == null)
                return;
            durationField.setValue((deadlineField.getValue().getTime() - beginField.getValue().getTime()) / ONEDAY);
        });
    }

    @Override
    public void ready() {
        super.ready();
        //кнопки Контроль и Готово
        btnControl.setEnabled(false);
        btnReady.setEnabled(true);
        if (getItem().getDone() != null && getItem().getDone()) {
            btnReady.setEnabled(false);
            btnControl.setEnabled(true);
        }
        if (getItem().getControl() != null && getItem().getControl()) {
            btnControl.setEnabled(false);
        }
    }

    private void calcDates(ValueChangeEvent e) {
        if (beginField.getValue() == null || durationField.getValue() == null)
            return;
        Date d = beginField.getValue();
        d.setTime((d.getTime() + ONEDAY * Double.valueOf(durationField.getRawValue()).longValue()));
        deadlineField.setValue(d);
    }

    public void onBtnReadyClick() {
        getItem().setDone(true);
        if (this.isModified()) {
            this.commitAndClose();
        }
        Tracker parentTracker = getItem().getParentBug();
        if (parentTracker == null)
            return;
        parentTracker = dataManager.reload(parentTracker, "_full");
        int countTask = parentTracker.getTask().size();
        int countDoneTask = 0;
        for (Task task : parentTracker.getTask()) {
            task = dataManager.reload(task, "tasks-performer-view");
            if (task.getDone() != null && task.getDone()) {
                countDoneTask++;
            }
        }
        if (countDoneTask == countTask) {
            Stage stage = getStage(parentTracker);
            WorkflowInstanceTask instanceTask = workflowService.getWorkflowInstanceTaskNN(parentTracker, stage);
            try {
                if (instanceTask != null) {
                    Map params = new HashMap();
                    params.put("toCheck", "true");
                    workflowService.finishTask(instanceTask, params);
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка обработки заявки", e);
            }
        }
        this.close(this.COMMIT_ACTION_ID, true);
    }

    public void onBtnControlClick() {
        getItem().setControl(true);
        if (this.isModified()) {
            this.commitAndClose();
        }
        Tracker parentTracker = getItem().getParentBug();
        if (parentTracker == null)
            return;
        parentTracker = dataManager.reload(parentTracker, "_full");
        int countTask = parentTracker.getTask().size();
        int countControlTask = 0;
        for (Task task : parentTracker.getTask()) {
            task = dataManager.reload(task, "tasks-performer-view");
            if (task.getControl() != null && task.getControl()) {
                countControlTask++;
            }
        }
        if (countTask == countControlTask) {
            Stage stage = getStage(parentTracker);
            WorkflowInstanceTask instanceTask = workflowService.getWorkflowInstanceTaskNN(parentTracker, stage);
            try {
                if (instanceTask != null) {
                    Map params = new HashMap();
                    params.put("isReady", "true");
                    workflowService.finishTask(instanceTask, params);
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка обработки заявки", e);
            }
        }
        this.close(this.COMMIT_ACTION_ID, true);
    }

    private Stage getStage(Tracker parentBug) {
        return dataManager.load(Stage.class)
                .query("select e from wfstp$Stage e where e.entityName = :entityName and e.name = :name")
                .parameter("entityName", parentBug.getMetaClass().getName())
                .parameter("name", parentBug.getStepName())
                .view("stage-process")
                .optional()
                .orElse(null);
    }


}