package com.company.scrumit.web.task;

import com.company.scrumit.entity.*;
import com.company.scrumit.entity.ProjectIdentificator;

import com.company.scrumit.entity.TaskType;
import com.company.scrumit.service.ProjectIdentificatorService;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.DataManager;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebLookupField;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebLookupField;


import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

public class TaskEdit extends AbstractEditor<Task> {
    private static final long ONEDAY = 24 * 60 * 60 * 1000;


    public static final String SCREEN_ID = " scrumit$Task.edit";

    @Named("fieldGroup.deadline")
    private DateField<Date> deadlineField;
    @Named("fieldGroup.begin")
    private DateField<Date> beginField;
    @Named("fieldGroup.duration")
    private TextField<Integer> durationField;
    @Named("fieldGroup.type")
    private WebLookupField typeField;
    @Inject
    private Button btnReady;
    @Inject
    private WorkflowService workflowService;
    @Inject
    private DataManager dataManager;
    @Inject
    private Datasource<Task> taskDs;
    @Inject
    private FieldGroup fieldGroup;
    @Inject
    private Datasource<ProjectIdentificator> projectIdentificatorDs;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private ProjectIdentificatorService projectIdentificatorService;
    @Named("fieldGroup.planningTime")
    private TextField<Double> planningTimeField;
    @Named("fieldGroup.taskClass")
    private LookupPickerField<TaskClass> taskClassField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        durationField.addValueChangeListener(this::calcDates);
        beginField.addValueChangeListener(this::calcDates);
        deadlineField.addValueChangeListener(e -> {
            if (beginField.getValue() == null)
                return;
            durationField.setValue((int) ((deadlineField.getValue().getTime() - beginField.getValue().getTime()) / ONEDAY));
        });
        typeField.addValueChangeListener(e -> {
            if (((HasValue.ValueChangeEvent) e).getValue().equals(TaskType.project)){
                ProjectIdentificator identificator = projectIdentificatorService.getProjectIdentificatorByProject(taskDs.getItem());
                if (identificator!=null){
                    projectIdentificatorDs.setItem(identificator);
                }else {
                    identificator = dataManager.create(ProjectIdentificator.class);
                    identificator.setProject(taskDs.getItem());
                    projectIdentificatorDs.setItem(identificator);

                }
                FieldGroup.FieldConfig field = fieldGroup.createField("identificator");
                field.setDatasource(projectIdentificatorDs);
                field.setProperty("identificator");
                field.setCaption(getMessage("project_id"));
                field.setComponent(componentsFactory.createComponent(TextField.class));
                ((TextField) field.getComponent()).setValue(projectIdentificatorDs.getItem().getIdentificator());
                field.setRequired(true);
                field.setVisible(true);
                field.setEnabled(true);
                ((TextField)field.getComponent()).addTextChangeListener(textChangeEvent -> {
                    projectIdentificatorDs.getItem().setIdentificator(textChangeEvent.getText());
                });
                fieldGroup.addField(field);

            }else {
                projectIdentificatorDs.setItem(null);
                FieldGroup.FieldConfig field = fieldGroup.getField("identificator");
                if (field!=null)
                    fieldGroup.removeField(field);
            }
        });
        taskClassField.addValueChangeListener(taskClassValueChangeEvent -> {
            TaskClass taskClass = (TaskClass) (((HasValue.ValueChangeEvent) taskClassValueChangeEvent).getValue());
            if (taskClass.getAverageDurationHours()!=null)
                planningTimeField.setValue(Double.valueOf(taskClass.getAverageDurationHours().toString()));
        });
    }


    @Override
    public void ready() {
        super.ready();
        //кнопки Контроль и Готово
        btnReady.setEnabled(true);
        if (getItem().getDone() != null && getItem().getDone()) {
            btnReady.setEnabled(false);
        }
        if (getItem().getControl() != null && getItem().getControl()) {
        }
    }

    private void calcDates(HasValue.ValueChangeEvent e) {
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
            WorkflowInstanceTask instanceTask = workflowService.getWorkflowInstanceTaskNN(getItem(), stage);
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