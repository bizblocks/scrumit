package com.company.scrumit.web.task;

import com.company.scrumit.entity.Estimation;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;

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
    }

    @Inject
    private HierarchicalDatasource<Task, UUID> tasksDs;

    @Inject
    private Metadata metadata;

    public void onBtnCreateInGroupClick() {
        Task t = metadata.create(Task.class);
        t.setShortdesc("");
        t.setTask(table.getSingleSelected());
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
        d.setTime((d.getTime() + ONEDAY * Double.valueOf(durationField.getValue()).longValue()));
        deadlineField.setValue(d);
    }

    public Component generateEstimationsCountCell(Entity entity) {
        long count = dataManager.load(Estimation.class)
                .query("select e from scrumit$Estimation e where e.task.id = :task")
                .view("_minimal")
                .parameter("task", entity)
                .list()
                .size();
        return new Table.PlainTextCell(String.valueOf(count));
    }
}