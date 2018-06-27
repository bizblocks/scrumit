package com.company.scrumit.web.task;

import com.company.scrumit.entity.Task;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class Massinput extends AbstractWindow {

    @Inject
    private LookupPickerField parent;

    @Inject
    private Button btnCreate;

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
    }

    public void onAdd(Component source) {
        if(null==parent.getValue())
        {
            showNotification(getMessage("Select parent first"), NotificationType.WARNING);
            return;
        }
        Task task = new Task();
        task.setTask(parent.getValue());
        newTasksDs.addItem(task);
    }

    public void onDel(Component source) {
        if(tab.getSelected()==null || tab.getSelected().size()==0)
        {
            showNotification(getMessage("Select items first"), NotificationType.WARNING);
            return;
        }
    }
}