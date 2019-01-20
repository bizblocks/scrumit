package com.company.scrumit.web.task;

import com.company.scrumit.entity.Priority;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
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
    @Inject
    private GridLayout grid;
    @Inject
    private ComponentsFactory componentsFactory;

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

        table.getDatasource().addItemChangeListener(e -> setupTestingPlan());
        initPhotoLibrary();
        table.getDatasource().addItemChangeListener(e -> {
            getAndShowImages();
        });
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
            task.setDone(true);
            dataManager.commit(task);
        });
    }

    private void calcDates(ValueChangeEvent e) {
        if (beginField.getValue() == null || durationField.getValue() == null)
            return;
        Date d = beginField.getValue();
        d.setTime((d.getTime() + ONEDAY * Double.valueOf(durationField.getRawValue()).longValue()));
        deadlineField.setValue(d);
    }

    @Override
    protected void initEditComponents(boolean enabled) {
        super.initEditComponents(enabled);
        setupTestingPlan();
    }

    private void setupTestingPlan(){
        Task item = (Task) getFieldGroup().getDatasource().getItem();
        Component testingPlan = grid.getComponent("testingPlan");
        grid.remove(testingPlan);

        if (item == null || item.getTestingPlan() == null || item.getTestingPlan().length() == 0) {
            showTestingPlanAsTextField();
        } else {
           showTestingPlanAsLink(item);
        }
    }

    private void showTestingPlanAsTextField(){
        Button btn = (Button) grid.getComponent("okBtn");
        btn.setCaption("OK");
        TextField textField = componentsFactory.createComponent(TextField.class);
        textField.setId("testingPlan");
        textField.setDatasource(taskDs, "testingPlan");
        textField.setWidth("100%");
        grid.add(textField, 0, 0);
    }

    private void showTestingPlanAsLink(Task item){
        Button btn = (Button) grid.getComponent("okBtn");
        btn.setCaption("Edit");
        Link link = componentsFactory.createComponent(Link.class);
        link.setUrl(item.getTestingPlan());
        link.setId("testingPlan");
        try {
            link.setCaption(item.getTestingPlan().substring(0, 40) + "...");
        } catch (Exception e) {
            link.setCaption(item.getTestingPlan());
        }
        link.setWidth("60%");
        link.setTarget("_blank");
        grid.add(link, 0, 0);
    }

    public void onOkBtn(){
        Task item = (Task) getFieldGroup().getDatasource().getItem();
        Component testingPlan = grid.getComponent("testingPlan");
        grid.remove(testingPlan);
        Button btn = (Button) grid.getComponent("okBtn");
        if (item == null || item.getTestingPlan() == null || item.getTestingPlan().length() == 0 || btn.getCaption().equals("Edit")) {
            showTestingPlanAsTextField();
        } else {
            showTestingPlanAsLink(item);
        }
    }

    @Inject
    private FileMultiUploadField multiUpload;
    @Inject
    private FileUploadingAPI fileUploadingAPI;
    @Inject
    private DataSupplier dataSupplier;
    @Inject
    private FlowBoxLayout thumbnailsBox;
    @Inject
    private FileLoader fileLoader;

    private List<FileDescriptor> imagesToDelete = new ArrayList<>();

    private void initPhotoLibrary() {
        getAndShowImages();
        initFilesUpload();
        getDsContext().addBeforeCommitListener(context -> context.addInstanceToCommit(getEntity()));
    }

    private Task getEntity() {
        return ((Task) getFieldGroup().getDatasource().getItem());
    }

    private void initFilesUpload() {
        multiUpload.addQueueUploadCompleteListener(() -> {
            if (getEntity().getVersion() == null) {
                showNotification(getMessage("entityCommitted"));
                dataManager.commit(getEntity());
                getDsContext().refresh();
            }
            for (Map.Entry<UUID, String> entry : multiUpload.getUploadsMap().entrySet()) {
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
                ((DatasourceImplementation) getFieldGroup().getDatasource()).setModified(true);
            }
            multiUpload.clearUploads();

        });

        multiUpload.addFileUploadErrorListener(event ->
                showNotification("File upload error", NotificationType.HUMANIZED));

    }

    private void getAndShowImages() {
        if (getEntity() == null) return;
        thumbnailsBox.removeAll();
        getEntity().getImages().forEach(this::addThumbnail);
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
                if (getLookupBox().isEnabled()) return;
                getEntity().getImages().remove(fd);
                imagesToDelete.add(fd);
                thumbnailsBox.remove(imageBox);
                ((DatasourceImplementation) getFieldGroup().getDatasource()).setModified(true);
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
    public void save() {
        super.save();
        imagesToDelete.forEach(img -> {
            try {
                fileLoader.removeFile(img);
            } catch (FileStorageException ignore) {
            }
        });
    }

    @Override
    protected void enableEditControls(boolean creating) {
        super.enableEditControls(creating);
        Component multiUploadButton = getComponent("multiUpload");
        multiUploadButton.setEnabled(true);
        if (getEntity().getVersion() == null) thumbnailsBox.removeAll();
    }

    @Override
    protected void disableEditControls() {
        super.disableEditControls();
        Component multiUploadButton = getComponent("multiUpload");
        multiUploadButton.setEnabled(false);
    }

}