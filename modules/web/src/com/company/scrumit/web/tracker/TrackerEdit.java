package com.company.scrumit.web.tracker;

import com.company.scrumit.entity.*;
import com.company.scrumit.service.UrlService;
import com.company.scrumit.web.task.TaskEdit;
import com.groupstp.workflowstp.entity.*;
import com.groupstp.workflowstp.util.EqualsUtils;
import com.groupstp.workflowstp.web.bean.WorkflowWebBean;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Link;
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
    private Table<Files> filesTable;

    @Inject
    private EntityStates entityStates;

    @Inject
    private GridLayout grid;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private UrlService urlService;

    private User user;
    private Stage stage;
    protected Workflow workflow;
    private WorkflowInstance workflowInstance;
    private WorkflowInstanceTask workflowInstanceTask;

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

        if (getItem().getTestingPlan() == null || getItem().getTestingPlan().length() == 0 || "Edit".equals(btn.getCaption())) {
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
        multiUpload.addQueueUploadCompleteListener(
                this::queueUploadComplete);

        multiUpload.addFileUploadErrorListener(event ->
                showNotification(getMessage("File upload error"), NotificationType.HUMANIZED));

        taskDs.refresh(Collections.singletonMap("project", TaskType.project));

        initWorkflow();

        tasksTable.expandAll();
    }

    private boolean initWorkflow() {
        if (stage != null && workflow != null) {//this is screen of one of stage
            if (EqualsUtils.equalAny(stage.getType(), StageType.USERS_INTERACTION, StageType.ARCHIVE)) {//we need to extend screen by stage
                if (workflowWebBean.isActor(user, stage)) {
                    try {
                        workflowWebBean.extendEditor(getItem(), this, workflowInstanceTask);
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

    /**
     * создаёт задачу из текущего инцидента
     */
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
                    boolean modifed = dataSource.isModified();
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
            for (Files files : filesTable.getSelected()) {
                AppConfig.createExportDisplay(this).show(files.getFile());
            }
        }
    }
    
    @Inject
    private TreeTable<Task> tasksTable;

    /**
     * создаёт подзадачу для текущей заадчи
     */
    public void onCreateSubTaskBtnClick() {
        Task subtask = dataManager.create(Task.class);
        Task task = tasksTable.getSingleSelected();

        if(task==null)
            return;

        try {
            subtask.setShortdesc("[subtask-"+subtask.getId()+"]");
            subtask.setTask(task);
            subtask.setParentBug(getItem());
            subtask.setPerformer(getItem().getPerformer());
            subtask.setBegin(task.getBegin());
            subtask.setDeadline(task.getDeadline());
            subtask.setType(TaskType.task);
            dataManager.commit(subtask);
        }catch (Exception e)
        {
            throw e;
        }
        subtask = dataManager.reload(subtask, "_local");

        try {
            subtask.setDuration(task.getDeadline().compareTo(task.getBegin()));
        }catch (Exception e) { }

        dataManager.commit(subtask);

        tasksTable.getDatasource().refresh();
        tasksTable.expand(task);
        tasksTable.scrollTo(subtask);
    }

    private void queueUploadComplete() {
        // save file to FileStorage
        // save file descriptor to database
        // add reloaded FileDescriptor
        //getItem().getFiles().add(file);
        // commit Foo to save changes
        // refresh datasource to show changes
        for (Map.Entry<UUID, String> entry : multiUpload.getUploadsMap().entrySet()) {
            UUID fileId = entry.getKey();
            String fileName = entry.getValue();
            FileDescriptor fd = fileUploadingAPI.getFileDescriptor(fileId, fileName);
            // save file to FileStorage
            try {
                fileUploadingAPI.putFileIntoStorage(fileId, fd);
            } catch (FileStorageException e) {
                throw new RuntimeException(getMessage("Error saving file to FileStorage"), e);
            }

            // save file descriptor to database
            FileDescriptor committedFd = dataSupplier.commit(fd);

            // add reloaded FileDescriptor
            Files file = dataManager.create(Files.class);
            file.setDescription(fd.getName() + ":" + fd.getSize());
            file.setEntity(getItem().getUuid());
            file.setFile(committedFd);
            dataManager.commit(file);
            //getItem().getFiles().add(file);
        }
        showNotification(getMessage("Uploaded files") + multiUpload.getUploadsMap().values(), NotificationType.HUMANIZED);
        multiUpload.clearUploads();

        // commit Foo to save changes
        Tracker committedFoo = dataSupplier.commit(getItem());
        setItem(committedFoo);

        // refresh datasource to show changes
        filesDs.refresh();
    }

    public void onRefreshTasksBtnClick() {
        tasksTable.getDatasource().refresh();
    }

    public void onMakeUrlBtnClick() {
        Tracker selectedTracker = trackerDs.getItem();

        String url = urlService.MakeOpenUrl(UrlService.Command.OPEN, SCREEN_ID, selectedTracker.getId());

        selectedTracker.setUrl(url);
    }
}



