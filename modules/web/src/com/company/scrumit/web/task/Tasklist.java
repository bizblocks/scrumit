package com.company.scrumit.web.task;

import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Tasklist extends EntityCombinedScreen {
    @Inject
    private TreeTable<Task> table;

    @Inject
    private DataManager dataManager;

    @Inject
    private HierarchicalDatasource<Task, UUID> tasksDs;

    public void onBtnCreateInGroupClick() {
        Task t = new Task();
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
}