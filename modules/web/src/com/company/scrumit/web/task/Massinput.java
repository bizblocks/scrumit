package com.company.scrumit.web.task;

import com.company.scrumit.entity.Priority;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class Massinput extends AbstractWindow {

    @Inject
    private LookupPickerField parent;

    @Inject
    private Table<Task> tab;

    @Inject
    private CollectionDatasource<Task, UUID> newTasksDs;


    /**
     * Called by the framework after creation of all components and before showing the screen.
     * <br> Override this method and put initialization logic here.
     *
     * @param params parameters passed from caller's code, usually from
     *               {@link #openWindow(String, WindowManager.OpenType)} and similar methods, or set in
     *               {@code screens.xml} for this registered screen
     */
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        parent.setValue(params.get("parent"));
        newTasksDs.addItemPropertyChangeListener(e -> {
            if(!"shortdesc".equals(e.getProperty()))
                return;
            if(!"".equals(e.getItem().getDescription()) && e.getItem().getDescription()!=null)
                return;
            e.getItem().setDescription(e.getValue().toString());
        });
    }

    public void onAdd(Component source) {
        if(null==parent.getValue())
        {
            showNotification(getMessage("Select parent first"), NotificationType.WARNING);
            return;
        }
        Task task = new Task();
        task.setTask(parent.getValue());
        task.setPriority(Priority.Middle);
        task.setType(TaskType.task);
        newTasksDs.addItem(task);
    }

    public void onDel(Component source) {
        if(tab.getSelected()==null || tab.getSelected().size()==0)
        {
            showNotification(getMessage("Select items first"), NotificationType.WARNING);
            return;
        }
    }

    @Inject
    private CollectionDatasource<Task, UUID> tasksDs;

    public void onCommit(Component ignore) {
        newTasksDs.commit();
        newTasksDs.refresh();
        tasksDs.refresh();
        UiEvent.push("taskRefresh");
    }
}