package com.company.scrumit.web.tracker;

import com.company.scrumit.entity.Performer;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Tracker;
import com.company.scrumit.web.task.TaskEdit;
import com.company.scrumit.web.task.Tasklist;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.web.gui.components.WebLookupPickerField;

import javax.faces.view.facelets.Metadata;
import javax.inject.Inject;
import javax.inject.Named;
import javax.management.Notification;
import java.util.Map;

public class TrackerBrowse extends EntityCombinedScreen {

    @Inject
    protected LookupPickerField project;

    @Inject
    protected CollectionDatasource taskDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        taskDs.refresh(ParamsMap.of("project", "project"));
    }


    public void createTask() {
        LookupPickerField lookupPickerField = ((LookupPickerField) getComponent("project"));
        final CollectionDatasource dataSource = lookupPickerField.getOptionsDatasource();
        final DataSupplier dataService = dataSource.getDataSupplier();
        final Entity item = dataService.newInstance(dataSource.getMetaClass());
        if (!project.equals(null)) {
            ((Task) item).setTask((Task) project.getValue());
            ((Task) item).setPerformer((Performer) ((Task) project.getValue()).getPerformer());
        }
        TaskEdit editor = (TaskEdit) lookupPickerField.getFrame().openEditor(item, WindowManager.OpenType.DIALOG);
        editor.getDialogOptions().setWidth(1000).setResizable(true);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (Window.COMMIT_ACTION_ID.equals(actionId) && editor instanceof Editor) {
                    Object item = ((TaskEdit) editor).getItem();
                    if (item instanceof Entity) {
                        if (!project.equals(null)) {

                            ((Task) item).setTask((Task) project);
                        }
                        Boolean modifed = dataSource.isModified();
                        dataSource.addItem((Entity) item);
                        ((DatasourceImplementation) dataSource).setModified(modifed);
                    }

                    lookupPickerField.setValue(item);
                }
                lookupPickerField.requestFocus();
            }
        });
    }


}