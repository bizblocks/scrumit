package com.company.scrumit.web.tracker;


import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class TrackerCreate  extends AbstractWindow {

    private Logger log = LoggerFactory.getLogger (TrackerCreate.class);

    @Inject
    private DataManager dataManager;

    @Inject
    private TreeTable<Task> tasksTable;

    @Inject
    protected Button closeBtn;

    @Inject
    private ComponentsFactory componentsFactory;

    private List<Task> selectedItemsList = new ArrayList<>();

    private HashMap<Task, CheckBox> selectedCheckboxList = new HashMap<>();

    private HierarchicalDatasource datasource;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        List<Task> taskList = (List<Task>) params.get("taskList");
        datasource = new DsBuilder()
                .setJavaClass(Task.class)
                .setId("generatedTaskListDS")
                .setRefreshMode(CollectionDatasource.RefreshMode.NEVER)
                .buildHierarchicalDatasource();

        datasource.setHierarchyPropertyName("parent");

        for (Task task : taskList) {
            this.getHierarchicalDatasource(task);
        }

        tasksTable.setDatasource(datasource);
        tasksTable.setVisible(true);
        tasksTable.addGeneratedColumn("checkBox", entity -> {
            CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
            Task task = (Task) tasksTable.getItemDatasource(entity).getItem();
            if (selectedItemsList.contains(task)) {
                checkBox.setValue(true);
            }
            checkBox.addValueChangeListener(e -> {
                if (Boolean.TRUE.equals(e.getValue())) {
                    selectedItemsList.add(task);
                } else {
                    selectedItemsList.remove(task);
                }
                updateRecursive(task, (boolean) e.getValue());
            });
            selectedCheckboxList.put(task, checkBox);
            return checkBox;
        });

    }

    private void getHierarchicalDatasource(Task task) {
        datasource.includeItem(task);
        List<Task> childrenTask = task.getChildren();
        if (childrenTask != null) {
            for (Task child : childrenTask) {
                child.setParent(task);
                getHierarchicalDatasource(child);
            }
        }
    }

    private void updateRecursive(Task task, boolean checked) {

        CheckBox checkBox = selectedCheckboxList.get(task);
        checkBox.setValue(checked);

        List<Task> children = task.getChildren();
        if (children != null) {
            for (Task childTask : children) {
                updateRecursive(childTask, checked);
            }
        }
    }

    public void closeWindow() {
        this.close(Window.CLOSE_ACTION_ID);
    }

    public void saveTasks() {
        for (Task task: selectedItemsList) {
            Task subtask = dataManager.create(Task.class);

            try {
                subtask.setDeadline(task.getDeadline());
                if (task.getParent() != null) {
                    subtask.setShortdesc("[subtask-" + subtask.getId() + "]");
                } else {
                    subtask.setShortdesc(String.valueOf(subtask.getId()));
                }
                dataManager.commit(subtask);
            } catch (Exception e) {
                log.error("ERROR: " + e.getMessage());
            }
        }
        this.close(Window.CLOSE_ACTION_ID);
    }
}
