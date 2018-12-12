package com.company.scrumit.web.task;

import com.company.scrumit.entity.Priority;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class Tasklist extends EntityCombinedScreen {
    private static final long ONEDAY = 24*60*60*1000;

    @Inject
    private TreeTable<Task> table;

    @Inject
    private DataManager dataManager;

    @Named("fieldGroup.duration")
    private TextField durationField;

    @Named("fieldGroup.deadline")
    private DateField deadlineField;

    @Named("fieldGroup.begin")
    private DateField beginField;
    @Named("fieldGroup.control")
    private CheckBox control;
    @Inject
    private HierarchicalDatasource<Task, UUID> tasksDs;
    @Inject
    private Datasource<Task> taskDs;
    @Inject
    private Metadata metadata;
    @Inject
    private CollectionDatasource trackerDs;
    @Inject
    private CheckBox checkSelect;
    @Inject
    private GridLayout grid;
    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        durationField.addValueChangeListener(this::calcDates);
        beginField.addValueChangeListener(this::calcDates);
        deadlineField.addValueChangeListener(e -> {
           if(beginField.getValue()==null)
               return;
           durationField.setValue((deadlineField.getValue().getTime()-beginField.getValue().getTime())/ONEDAY);
        });
        addBeforeCloseWithCloseButtonListener(event -> table.getDatasource().commit());
        addBeforeCloseWithShortcutListener(event -> table.getDatasource().commit());
        
        checkSelect.addValueChangeListener(e -> table.setTextSelectionEnabled((Boolean) e.getValue()));

        Datasource editDs = getFieldGroup().getDatasource();
        table.getDatasource().addItemChangeListener(e -> {
            setupTestingPlan();
        });
    }


    public void onBtnCreateInGroupClick() {
        Task t = metadata.create(Task.class);
        t.setShortdesc("");
        t.setTask(table.getSingleSelected());
        t.setPriority(Priority.Middle);
        t.setType(TaskType.task);
        t.setDuration(1);
        dataManager.commit(t);
        tasksDs.refresh();
    }

    public void onMassInput(Component source) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("parent", table.getSingleSelected());
        openWindow("massInput", WindowManager.OpenType.DIALOG, map);
    }

    public void onBtnDoneClick() {
        Set<Task> tasks = table.getSelected();
        tasks.forEach(task -> {
            task.setDone(true);
            dataManager.commit(task);
        });
    }

    private void calcDates(ValueChangeEvent e) {
        if (beginField.getValue() == null || durationField.getValue() == null)
            return;
        Date d = beginField.getValue();
        d.setTime((d.getTime() + ONEDAY * Double.valueOf(durationField.getRawValue()).longValue()));
        deadlineField.setValue(d);
    }

    @Override
    protected void initEditComponents(boolean enabled) {
        super.initEditComponents(enabled);
        setupTestingPlan();
    }

    private void setupTestingPlan(){
        Task item = (Task) getFieldGroup().getDatasource().getItem();
        Component testingPlan = grid.getComponent("testingPlan");
        grid.remove(testingPlan);

        if (item == null || item.getTestingPlan() == null || item.getTestingPlan().length() == 0) {
            showTestingPlanAsTextField();
        } else {
           showTestingPlanAsLink(item);
        }
        
    }

    private void showTestingPlanAsTextField(){
        Button btn = (Button) grid.getComponent("okBtn");
        btn.setCaption("OK");
        TextField textField = componentsFactory.createComponent(TextField.class);
        textField.setId("testingPlan");
        textField.setDatasource(taskDs, "testingPlan");
        textField.setWidth("100%");
        grid.add(textField, 0, 0);
    }

    private void showTestingPlanAsLink(Task item){
        Button btn = (Button) grid.getComponent("okBtn");
        btn.setCaption("Edit");
        Link link = componentsFactory.createComponent(Link.class);
        link.setUrl(item.getTestingPlan());
        link.setId("testingPlan");
        try {
            link.setCaption(item.getTestingPlan().substring(0, 40) + "...");
        } catch (Exception e) {
            link.setCaption(item.getTestingPlan());
        }
        link.setWidth("60%");
        link.setTarget("_blank");
        grid.add(link, 0, 0);
    }

    public void onOkBtn(){
        Task item = (Task) getFieldGroup().getDatasource().getItem();
        Component testingPlan = grid.getComponent("testingPlan");
        grid.remove(testingPlan);
        Button btn = (Button) grid.getComponent("okBtn");
        if (item == null || item.getTestingPlan() == null || item.getTestingPlan().length() == 0 || btn.getCaption().equals("Edit")) {
            showTestingPlanAsTextField();
        } else {
            showTestingPlanAsLink(item);
        }
    }
}