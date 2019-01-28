package com.company.scrumit.web.tracker;

import com.company.scrumit.entity.Priority;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.company.scrumit.entity.Tracker;
import com.company.scrumit.web.task.TaskEdit;
import com.groupstp.workflowstp.dto.WorkflowExecutionContext;
import com.groupstp.workflowstp.entity.*;
import com.groupstp.workflowstp.service.WorkflowService;
import com.groupstp.workflowstp.util.EqualsUtils;
import com.groupstp.workflowstp.web.bean.WorkflowWebBean;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//todo
//переделать на новую библиотеку воркфлоу
public class TrackerEdit extends AbstractEditor<Tracker> {
    public static final String SCREEN_ID = "scrumit$Tracker.edit";

    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private DataManager dataManager;

    @Inject
    private LookupPickerField project;

    @Inject
    private WorkflowWebBean workflowWebBean;

    @Inject
    private Scripting scripting;
    @Inject
    private WorkflowService workflowService;
    @Inject
    protected TextField shortdesc;

    @Inject
    protected RichTextArea description;


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
    private GridLayout grid;

    @Inject
    private ComponentsFactory componentsFactory;

    protected User user;
    protected Stage stage;
    protected Workflow workflow;
    protected WorkflowInstance workflowInstance;
    protected WorkflowInstanceTask workflowInstanceTask;

    @Override
    protected void initNewItem(Tracker item) {
        //item.setFiles(new ArrayList<>());
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    public void postInit() {
        super.postInit();
        setupTestingPlan();
        user = getUser();
        stage = getStage();
        workflow = getWorkflow();
        workflowInstance = getWorkflowInstance();
        workflowInstanceTask = getWorkflowInstanceTask();
    }

    @Nullable
    private User getUser() {
        User user = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        if (user != null) {
            user = dataManager.reload(user, "user-with-roles");
        }
        return user;
    }

    @Nullable
    private Stage getStage() {
        if (!StringUtils.isEmpty(getItem().getStepName())) {
            return dataManager.load(Stage.class)
                    .query("select e from wfstp$Stage e where e.entityName = :entityName and e.name = :name")
                    .parameter("entityName", getItem().getMetaClass().getName())
                    .parameter("name", getItem().getStepName())
                    .view("stage-process")
                    .optional()
                    .orElse(null);
        }
        return null;
    }

    @Nullable
    private Workflow getWorkflow() {
        return getItem().getWorkflow();
    }

    @Nullable
    private WorkflowInstance getWorkflowInstance() {
        if (workflow != null && !PersistenceHelper.isNew(getItem())) {
            return dataManager.load(WorkflowInstance.class)
                    .query("select e from wfstp$WorkflowInstance e where e.entityName = :entityName and e.entityId = :entityId and e.workflow.id = :workflowId order by e.createTs desc")
                    .parameter("entityName", getItem().getMetaClass().getName())
                    .parameter("entityId", getItem().getId().toString())
                    .parameter("workflowId", workflow.getId())
                    .view("workflowInstance-process")
                    .optional()
                    .orElse(null);
        }
        return null;
    }

    @Nullable
    private WorkflowInstanceTask getWorkflowInstanceTask() {
        if (workflowInstance != null && stage != null) {
            return dataManager.load(WorkflowInstanceTask.class)
                    .query("select e from wfstp$WorkflowInstanceTask e " +
                            "join wfstp$Step s on e.step.id = s.id " +
                            "join wfstp$Stage ss on s.stage.id = ss.id " +
                            "where e.instance.id = :instanceId and ss.id = :stageId order by e.createTs desc")
                    .parameter("instanceId", workflowInstance.getId())
                    .parameter("stageId", stage.getId())
                    .view("workflowInstanceTask-process")
                    .optional()
                    .orElse(null);
        }
        return null;
    }

    private void setupTestingPlan() {
        Button btn = (Button) grid.getComponent("okBtn");
        Component testingPlan = grid.getComponent("testingPlan");
        grid.remove(testingPlan);

        if (getItem().getTestingPlan() == null || getItem().getTestingPlan().length() == 0 || btn.getCaption().equals("Edit")) {
            btn.setCaption("OK");
            TextField textField = componentsFactory.createComponent(TextField.class);
            textField.setId("testingPlan");
            textField.setDatasource(trackerDs, "testingPlan");
            textField.setWidth("100%");
            grid.add(textField, 4, 3);
        } else {
            btn.setCaption("Edit");
            Link link = componentsFactory.createComponent(Link.class);
            link.setUrl(getItem().getTestingPlan());
            link.setId("testingPlan");
            try {
                link.setCaption(getItem().getTestingPlan().substring(0, 40) + "...");
            } catch (Exception e) {
                link.setCaption(getItem().getTestingPlan());
            }
            link.setWidth("60%");
            link.setTarget("_blank");
            grid.add(link, 4, 3);
        }
    }

    public void onOkBtn() {
        setupTestingPlan();
    }

    @Override
    public boolean isModified() {
        return trackerDs.isModified();
    }


    @Override
    public void ready() {
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
                //getItem().getFiles().add(committedFd);
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

        initWorkflow();
    }

    protected boolean initWorkflow() {
        if (stage != null && workflow != null) {//this is screen of one of stage
            if (EqualsUtils.equalAny(stage.getType(), StageType.USERS_INTERACTION, StageType.ARCHIVE)) {//we need to extend screen by stage
                if (workflowWebBean.isActor(user, stage)) {
                    try {
                        workflowWebBean.extendEditor(stage, getItem(), this, workflowInstance, workflowInstanceTask);
                    } catch (Exception e) {
                        String message = getMessage("errorOnScreenExtension");
                        close(CLOSE_ACTION_ID, true);
                        throw new RuntimeException(message);
                    }
                }
            }
        }
        return true;
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
                item.setTask(project.getValue());
                if (((Task) project.getValue()).getPerformer() != null)
                    item.setPerformer(((Task) project.getValue()).getPerformer());
            }
            if (shortdesc.getValue() != null) {
                item.setShortdesc(shortdesc.getValue());
            }
            if (description.getValue() != null)
                item.setDescription(description.getValue());
        }
        TaskEdit editor = (TaskEdit) lookupPickerField.getFrame().openEditor(item, WindowManager.OpenType.DIALOG);
        ((LookupField) ((FieldGroup) editor.getComponent("fieldGroup")).getField("type").getComponent()).setValue(TaskType.task);
        ((LookupField) ((FieldGroup) editor.getComponent("fieldGroup")).getField("priority").getComponent()).setValue(Priority.Middle);
        editor.getDialogOptions().setResizable(true);
        editor.addCloseListener(actionId -> {
            if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                Object item1 = editor.getItem();
                if (item1 != null) {
                    Boolean modifed = dataSource.isModified();
                    dataSource.addItem((Entity) item1);
                    ((DatasourceImplementation) dataSource).setModified(modifed);
                }
                dataSource.addItem((Entity) item1);
                dataSource.refresh();
            }
            lookupPickerField.requestFocus();
        });
    }

    public void download() {
        if (filesTable.getSelected().size() > 0) {
            for (FileDescriptor fileDescriptor : filesTable.getSelected()) {
                AppConfig.createExportDisplay(this).show(fileDescriptor);
            }
        }
    }
}



