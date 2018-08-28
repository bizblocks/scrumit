package com.company.scrumit.web.tracker;

import com.company.scrumit.entity.*;
import com.company.scrumit.web.task.TaskEdit;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class TrackerEdit extends AbstractEditor<Tracker> {
    @Inject
    private LookupPickerField project;

    @Inject
    protected TextField shortdesc;

    @Inject
    protected RichTextArea description;

    @Inject
    protected LookupField status;

    @Inject
    protected CollectionDatasource taskDs;

    @Inject
    protected Datasource<Tracker> trackerDs;

    @Inject
    private CollectionDatasource<FileDescriptor, UUID> filesDs;
    @Inject
    private FileMultiUploadField multiUpload;

    @Inject
    private FileUploadingAPI fileUploadingAPI;

    @Inject
    private DataSupplier dataSupplier;

    @Inject
    private Table<FileDescriptor> filesTable;

    @Inject
    private EntityStates entityStates;

    @Inject
    private HierarchicalDatasource taskParentBugDs;

    @Inject
    private TabSheet tabSheet;

    @Override
    protected void initNewItem(Tracker item) {
        item.setFiles(new ArrayList<>());
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    public boolean isModified() {
        return trackerDs.isModified();
    }


    @Override
    public void ready() {
        for (TabSheet.Tab tab: tabSheet.getTabs()) {
            if (tab.getName().equals("taskTab"))
                tab.setCaption("Tasks - " + taskParentBugDs.size());
        }
        multiUpload.addQueueUploadCompleteListener(() -> {
            for (Map.Entry<UUID, String> entry : multiUpload.getUploadsMap().entrySet()) {
                UUID fileId = entry.getKey();
                String fileName = entry.getValue();
                FileDescriptor fd = fileUploadingAPI.getFileDescriptor(fileId, fileName);
                // save file to FileStorage
                try {
                    fileUploadingAPI.putFileIntoStorage(fileId, fd);
                } catch (FileStorageException e) {
                    throw new RuntimeException("Error saving file to FileStorage", e);
                }

                // save file descriptor to database
                FileDescriptor committedFd = dataSupplier.commit(fd);

                // add reloaded FileDescriptor
                getItem().getFiles().add(committedFd);
            }
            showNotification("Uploaded files: " + multiUpload.getUploadsMap().values(), NotificationType.HUMANIZED);
            multiUpload.clearUploads();

            // commit Foo to save changes
            Tracker committedFoo = dataSupplier.commit(getItem());
            setItem(committedFoo);

            // refresh datasource to show changes
            filesDs.refresh();
        });

        multiUpload.addFileUploadErrorListener(event ->
                showNotification("File upload error", NotificationType.HUMANIZED));

        taskDs.refresh(Collections.singletonMap("project", TaskType.project));
    }

    public void createTask() {
        LookupPickerField lookupPickerField = ((LookupPickerField) getComponent("project"));
        final CollectionDatasource dataSource = lookupPickerField.getOptionsDatasource();
        final DataSupplier dataService = dataSource.getDataSupplier();
        final Task item = dataService.newInstance(dataSource.getMetaClass());
        if (entityStates.isNew(getItem())) {
            showNotification("Please, save the object.", NotificationType.WARNING);
            return;
        }
        if (entityStates.isDetached(getItem())) {
            item.setParentBug(getItem());
            if (project.getValue() != null) {
                item.setTask((Task) project.getValue());
                if (((Task) project.getValue()).getPerformer() != null)
                    item.setPerformer((Performer) ((Task) project.getValue()).getPerformer());
            }
            if (shortdesc.getValue() != null) {
                item.setShortdesc(shortdesc.getValue());
            }
            if (description.getValue() != null)
                item.setDescription(description.getValue());
        }
        TaskEdit editor = (TaskEdit) lookupPickerField.getFrame().openEditor(item, WindowManager.OpenType.DIALOG);
        ((LookupField)((FieldGroup)editor.getComponent("fieldGroup")).getField("type").getComponent()).setValue(TaskType.task);
        ((LookupField)((FieldGroup)editor.getComponent("fieldGroup")).getField("priority").getComponent()).setValue(Priority.Middle);
        editor.getDialogOptions().setResizable(true);
        editor.addCloseListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (Window.COMMIT_ACTION_ID.equals(actionId) && editor instanceof Editor) {
                    Object item = ((TaskEdit) editor).getItem();
                    if (item instanceof Entity) {
                        Boolean modifed = dataSource.isModified();
                        dataSource.addItem((Entity) item);
                        ((DatasourceImplementation) dataSource).setModified(modifed);
                    }
                    dataSource.addItem((Entity) item);
                    dataSource.refresh();
                }
                lookupPickerField.requestFocus();
            }
        });
    }

    public void download() {
        if (filesTable.getSelected().size() >0) {
            for(FileDescriptor fileDescriptor: filesTable.getSelected()) {
                AppConfig.createExportDisplay(this).show(fileDescriptor);
            }
        }
    }
}



