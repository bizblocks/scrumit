package com.company.scrumit.web.task;

import com.company.scrumit.entity.Priority;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.company.scrumit.entity.Tracker;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.service.WorkflowService;
import com.groupstp.workflowstp.web.util.WebUiHelper;
import com.groupstp.workflowstp.web.util.data.ColumnGenerator;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class TaskList extends EntityCombinedScreen {
    private static final long ONEDAY = 24 * 60 * 60 * 1000;

    @Inject
    private TreeTable<Task> table;

    @Inject
    private DataManager dataManager;

    @Inject
    private WorkflowService workflowService;

    @Inject
    private WebUiHelper webUiHelper;
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
    private CheckBox checkSelect;
    @Inject
    private GridLayout grid;
    @Named("fieldGroup.task")
    private PickerField taskField;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private RichTextArea description;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        durationField.addValueChangeListener(this::calcDates);
        beginField.addValueChangeListener(this::calcDates);
        deadlineField.addValueChangeListener(e -> {
            if (beginField.getValue() == null)
                return;
            durationField.setValue((deadlineField.getValue().getTime() - beginField.getValue().getTime()) / ONEDAY);
        });
        addBeforeCloseWithCloseButtonListener(event -> table.getDatasource().commit());
        addBeforeCloseWithShortcutListener(event -> table.getDatasource().commit());

        checkSelect.addValueChangeListener(e -> table.setTextSelectionEnabled((Boolean) e.getValue()));

        Datasource editDs = getFieldGroup().getDatasource();
        table.getDatasource().addItemChangeListener(e -> {
            setupTestingPlan();
        });

        //добавляет возможность моментального редактирования по двойному клику
        initColumns();

    }

    private void initColumns() {
        webUiHelper.showColumns(table,
                getAllProperties(), getEditableProperties(),
                getColumnGenerators(), false);
    }

    private List<String> getAllProperties() {
        return Arrays.asList("shortdesc", "priority", "done", "control", "description", "performer", "testingPlan");
    }

    private List<String> getEditableProperties() {
        return Arrays.asList("shortdesc", "priority", "performer");
    }

    private Map<String, ColumnGenerator> getColumnGenerators() {
        Map<String, ColumnGenerator> result = new HashMap<>();
        return result;
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
        commitIfNeed(table);
        tasks.forEach(task -> {
            task.setDone(true);
            dataManager.commit(task);
            task = dataManager.reload(task, "tasks-full");
            Tracker parentTracker = task.getParentBug();
            if (parentTracker == null)
                return;
            parentTracker = dataManager.reload(parentTracker, "_full");
            int countTask = parentTracker.getTask().size();
            int countDoneTask = 0;
            for (Task trackerTask : parentTracker.getTask()) {
                trackerTask = dataManager.reload(trackerTask, "tasks-performer-view");
                if (trackerTask.getDone() != null && trackerTask.getDone()) {
                    countDoneTask++;
                }
            }
            if (countDoneTask == countTask) {
                Stage stage = getStage(parentTracker);
                WorkflowInstanceTask instanceTask = workflowService.loadLastProcessingTask(parentTracker, stage);
                try {
                    if (instanceTask != null) {
                        Map params = new HashMap();
                        params.put("toCheck", "true");
                        workflowService.finishTask(instanceTask, params);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка обработки заявки", e);
                }
            }
        });
        table.getDatasource().refresh();
    }


    public void onBtnChecked() {
        Set<Task> tasks = table.getSelected();
        commitIfNeed(table);
        tasks.forEach(task -> {
            task.setControl(true);
            dataManager.commit(task);
            task = dataManager.reload(task, "tasks-full");
            Tracker parentTracker = task.getParentBug();
            if (parentTracker == null)
                return;
            parentTracker = dataManager.reload(parentTracker, "_full");
            int countTask = parentTracker.getTask().size();
            int countControlTask = 0;
            for (Task trackerTask : parentTracker.getTask()) {
                trackerTask = dataManager.reload(trackerTask, "tasks-performer-view");
                if (trackerTask.getControl() != null && trackerTask.getControl()) {
                    countControlTask++;
                }
            }
            if (countTask == countControlTask) {
                Stage stage = getStage(parentTracker);
                WorkflowInstanceTask instanceTask = workflowService.loadLastProcessingTask(parentTracker, stage);
                try {
                    if (instanceTask != null) {
                        Map params = new HashMap();
                        params.put("isReady", "true");
                        workflowService.finishTask(instanceTask, params);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка обработки заявки", e);
                }
            }
        });
        table.getDatasource().refresh();
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
        //блокируем/разблокируем поле описания
        description.setEnabled(enabled);
    }

    private static void commitIfNeed(Table<Task> taskTable) {
        if (taskTable.getDatasource().isModified()) {
            taskTable.getDatasource().commit();
        }
    }

    private void setupTestingPlan() {
        Task item = (Task) getFieldGroup().getDatasource().getItem();
        Component testingPlan = grid.getComponent("testingPlan");
        grid.remove(testingPlan);

        if (item == null || item.getTestingPlan() == null || item.getTestingPlan().length() == 0) {
            showTestingPlanAsTextField();
        } else {
            showTestingPlanAsLink(item);
        }

    }

    private void showTestingPlanAsTextField() {
        Button btn = (Button) grid.getComponent("okBtn");
        btn.setCaption("OK");
        TextField textField = componentsFactory.createComponent(TextField.class);
        textField.setId("testingPlan");
        textField.setDatasource(taskDs, "testingPlan");
        textField.setWidth("100%");
        grid.add(textField, 0, 0);
    }

    private void showTestingPlanAsLink(Task item) {
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

    public void onOkBtn() {
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

    private Stage getStage(Tracker parentBug) {
        return dataManager.load(Stage.class)
                .query("select e from wfstp$Stage e where e.entityName = :entityName and e.name = :name")
                .parameter("entityName", parentBug.getMetaClass().getName())
                .parameter("name", parentBug.getStepName())
                .view("stage-process")
                .optional()
                .orElse(null);
    }


    public void onBtnHideArchive(Component source) {
        String q = tasksDs.getQuery();
        q = q + " where e.control = false or e.control is null";
        tasksDs.setQuery(q);
        tasksDs.refresh();
    }
}