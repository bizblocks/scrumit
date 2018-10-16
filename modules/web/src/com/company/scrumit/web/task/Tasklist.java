package com.company.scrumit.web.task;

import com.company.scrumit.entity.*;
import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import org.springframework.context.event.EventListener;

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

    @EventListener
    public void onUiEvent(UiEvent event) {
        if ("taskRefresh".equals(event.getSource())) {
            getDsContext().refresh();
        }
    }

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
            if (task.getType() == TaskType.testing) {
                task.setDone(true);
                dataManager.commit(task);
            } else if (!task.getDone()) {
                task.setDone(true);
                dataManager.commit(task);
                processDoneTask(task);
            }
        });
        getDsContext().refresh();
    }

    private void calcDates(ValueChangeEvent e) {
        if (beginField.getValue() == null || durationField.getValue() == null)
            return;
        Date d = beginField.getValue();
        d.setTime((d.getTime() + ONEDAY * Double.valueOf(durationField.getRawValue()).longValue()));
        deadlineField.setValue(d);
    }

    private void processDoneTask(Task parentTask) {
        if (parentTask.getDone()) {
            Task task = AppBeans.get(Metadata.class).create(Task.class);
            task.setShortdesc("Testing: " + parentTask.getShortdesc() + "");
            task.setTop(parentTask.getTop());
            task.setTask(parentTask);
            task.setDone(false);
            task.setControl(false);
            task.setPriority(parentTask.getPriority());
            task.setTeams(parentTask.getTeams());
            task.setType(TaskType.testing);
            task.setTracker(parentTask.getTracker());
            task.setPerformer(getQATesterForProject(parentTask));
            dataManager.commit(task);
            showNotification(getMessage("taskForTestingCreated"), NotificationType.TRAY);
        }
    }

    private Performer getQATesterForProject(Task task) {

        List<ProjectRole> rolesQATester = dataManager.loadList(LoadContext.create(ProjectRole.class).setQuery(LoadContext.createQuery(
                "select role from scrumit$ProjectRole role where role.type=:type")
                .setParameter("type", ProjectRoleType.QATester))
                .setView("projectRole-full"));

        ProjectRole roleAppropriated = null;
        for (ProjectRole projectRole : rolesQATester) {
            if (checkProjectRecur(projectRole, task)) {
                roleAppropriated = projectRole;
                break;
            }
        }
        return roleAppropriated == null ? null : roleAppropriated.getPerformer();
    }

    private boolean checkProjectRecur(ProjectRole role, Task task) {
        if (task.getTask() == null) {
            return false;
        } else if (role.getProject().equals(task.getTask())) {
            return true;
        } else {
            return checkProjectRecur(role, task.getTask());
        }
    }
}