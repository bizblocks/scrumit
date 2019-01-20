package com.company.scrumit.web.tracker;

import com.company.scrumit.entity.Priority;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.company.scrumit.entity.Tracker;
import com.company.scrumit.web.task.TaskEdit;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.*;

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

    @Inject
    private GridLayout grid;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    protected void initNewItem(Tracker item) {
        //item.setFiles(new ArrayList<>());
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    public void postInit(){
        super.postInit();
        setupTestingPlan();
        initPhotoLibrary();
    }

    private void setupTestingPlan(){
        Button btn = (Button) grid.getComponent("okBtn");
        Component testingPlan = grid.getComponent("testingPlan");
        grid.remove(testingPlan);

        if(getItem().getTestingPlan()==null || getItem().getTestingPlan().length()==0 || btn.getCaption().equals("Edit")) {
            btn.setCaption("OK");
            TextField textField = componentsFactory.createComponent(TextField.class);
            textField.setId("testingPlan");
            textField.setDatasource(trackerDs, "testingPlan");
            textField.setWidth("100%");
            grid.add(textField, 4, 3);
        }
        else{
            btn.setCaption("Edit");
            Link link = componentsFactory.createComponent(Link.class);
            link.setUrl(getItem().getTestingPlan());
            link.setId("testingPlan");
            try{
                link.setCaption(getItem().getTestingPlan().substring(0,40) + "...");
            }
            catch(Exception e){
                link.setCaption(getItem().getTestingPlan());
            }
            link.setWidth("60%");
            link.setTarget("_blank");
            grid.add(link, 4, 3);
        }
    }

    public void onOkBtn(){
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
        ((LookupField)((FieldGroup)editor.getComponent("fieldGroup")).getField("type").getComponent()).setValue(TaskType.task);
        ((LookupField)((FieldGroup)editor.getComponent("fieldGroup")).getField("priority").getComponent()).setValue(Priority.Middle);
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
        if (filesTable.getSelected().size() >0) {
            for(FileDescriptor fileDescriptor: filesTable.getSelected()) {
                AppConfig.createExportDisplay(this).show(fileDescriptor);
            }
        }
    }

    @Inject
    private FileMultiUploadField multiUploadImages;
    @Inject
    private FlowBoxLayout thumbnailsBox;
    @Inject
    private FileLoader fileLoader;

    private List<FileDescriptor> imagesToDelete = new ArrayList<>();

    private void initPhotoLibrary() {
        getAndShowImages();
        initImagesUpload();
        getDsContext().addBeforeCommitListener(context -> context.addInstanceToCommit(getEntity()));
    }

    private Tracker getEntity() {
        return getItem();
    }

    private void getAndShowImages() {
        getEntity().getImages().forEach(this::addThumbnail);
    }

    private void initImagesUpload() {
        multiUploadImages.addQueueUploadCompleteListener(() -> {
            for (Map.Entry<UUID, String> entry : multiUploadImages.getUploadsMap().entrySet()) {
                UUID fileId = entry.getKey();
                String fileName = entry.getValue();
                FileDescriptor fd = fileUploadingAPI.getFileDescriptor(fileId, fileName);
                // save file to FileStorage
                try {
                    fileUploadingAPI.putFileIntoStorage(fileId, fd);
                } catch (FileStorageException e) {
                    new RuntimeException("Error saving file to FileStorage", e);
                }
                // save file descriptor to database
                FileDescriptor committedFd = dataSupplier.commit(fd);

                addThumbnail(committedFd);
                getEntity().getImages().add(fd);
                ((DatasourceImplementation) trackerDs).setModified(true);
            }
            multiUpload.clearUploads();

        });
        multiUpload.addFileUploadErrorListener(event ->
                showNotification("File upload error", NotificationType.HUMANIZED));
    }

    private void addThumbnail(FileDescriptor fd) {
        Image image = componentsFactory.createComponent(Image.class);
        image.setSource(FileDescriptorResource.class).setFileDescriptor(fd);
        image.setWidth("200px");
        image.setScaleMode(Image.ScaleMode.SCALE_DOWN);
        image.setHeight("200px");

        HBoxLayout imageBox = componentsFactory.createComponent(HBoxLayout.class);
        imageBox.add(image);
        VBoxLayout innerButtonBox = componentsFactory.createComponent(VBoxLayout.class);
        imageBox.add(innerButtonBox);

        Button clearButton = componentsFactory.createComponent(Button.class);
        clearButton.setCaption("X");
        clearButton.setAction(new BaseAction("Remove") {
            @Override
            public void actionPerform(Component component) {
                getEntity().getImages().remove(fd);
                imagesToDelete.add(fd);
                thumbnailsBox.remove(imageBox);
                ((DatasourceImplementation) trackerDs).setModified(true);
            }
        });
        innerButtonBox.add(clearButton);

        Button showButton = componentsFactory.createComponent(Button.class);
        showButton.setCaption(getMessage("show"));
        showButton.setAction(new BaseAction("show") {
            @Override
            public void actionPerform(Component component) {
                AppBeans.get(ExportDisplay.class).show(fd);
            }
        });
        innerButtonBox.add(showButton);

        thumbnailsBox.add(imageBox);
    }

    @Override
    public boolean commit() {
        imagesToDelete.forEach(img -> {
            try {
                fileLoader.removeFile(img);
            } catch (FileStorageException ignore) {
            }
        });
        return super.commit();
    }
}



