package com.company.scrumit.web.task.workflow.frame;

import com.company.scrumit.entity.*;
import com.company.scrumit.service.TrackerService;
import com.company.scrumit.web.task.TabType;
import com.company.scrumit.web.task.TaskEdit;
import com.company.scrumit.web.tracker.TrackerEdit;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.service.WorkflowService;
import com.groupstp.workflowstp.web.bean.WorkflowWebBean;
import com.groupstp.workflowstp.web.components.AbstractXmlDescriptorFrame;
import com.groupstp.workflowstp.web.components.ExternalSelectionGroupTable;
import com.groupstp.workflowstp.web.util.action.AlwaysActiveAction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.*;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class TaskWorkflowBrowseTableFrame extends AbstractXmlDescriptorFrame {

    public static final String SCREEN_ID = "Task-workflow-table";
    public static final String TAB_TYPE = "tabType";
    public static final String VIEW_ONLY = "viewOnly";
    public static final String STAGE = "stage";
    public static final String USER = "user";

    @Inject
    private HierarchicalDatasource<Task, UUID> taskDs;
    @Inject
    private TreeTable<Task> taskTable;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    private ButtonsPanel buttonsPanel;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected WorkflowWebBean workflowWebBean;
    @WindowParam(name = TAB_TYPE, required = true)
    protected TabType tabType;

    @WindowParam(name = VIEW_ONLY, required = true)
    protected Boolean viewOnly;
    @Inject
    private UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        //присвоение идентификатора фрейму
        initFiltersId(params);

        //инициализация запроса
        if (TabType.NEW.equals(tabType)) {
            String query = initSqlQuery(params);
            replaceTable(query);
        } else {
            initSqlQuery(params);
        }

        //инициализация поведения
        initTableBehaviour(params);

        //tab opened for view only
        if (Boolean.TRUE.equals(viewOnly)) {
            taskTable.setEditable(false);
            if (!CollectionUtils.isEmpty(taskTable.getActions())) {
                for (Action action : taskTable.getActions()) {
                    if (!(action instanceof AlwaysActiveAction) &&
                            !(action instanceof RefreshAction) &&
                            !(action instanceof ExcelAction) /*&&
                            !(action instanceof EditAction)*/) {
                        action.setEnabled(false);
                    }
                }
            }
        }

    }


    private void initFiltersId(Map<String, Object> params) {
        //since many instances of this frame can be created for one browse screen we need to specify different frame id
        //to make generic filters work correctly
        String id = getTabTypeKey(params);
        setId(id);
    }

    private String getTabTypeKey(Map<String, Object> params) {
        switch (tabType) {
            case NEW: {
                return "def";
            }
            case WORKFLOW: {
                return transformIdToKey(params.get(STAGE) == null ? null : ((Stage) params.get(STAGE)).getId());
            }
        }
        return null;
    }

    private String transformIdToKey(UUID uuid) {
        return uuid == null ? StringUtils.EMPTY : uuid.toString().replaceAll("\\W", StringUtils.EMPTY);
    }

    private String initSqlQuery(Map<String, Object> params) {
        String sqlQuery;
        switch (tabType) {
            case NEW:
                sqlQuery = "select e from scrumit$Tracker e ";
                StringBuilder trackerQuery = new StringBuilder(sqlQuery);
                trackerQuery.append(" where ");
                addTeamFiltration(trackerQuery, params, true);
                sqlQuery = trackerQuery.toString();
                break;
            case WORKFLOW:
                sqlQuery = "select e from scrumit$Task e";
                Stage stage = (Stage) params.get(STAGE);
                StringBuilder workflowQuery = new StringBuilder(sqlQuery);
                workflowQuery.append(" where ");
                workflowQuery.append("e.stepName='").append(stage.getName()).append("' ");
                workflowQuery.append(" and ");

                addTeamFiltration(workflowQuery, params, false);
                sqlQuery = workflowQuery.toString();
                taskDs.setQuery(sqlQuery);
                break;
            default: {
                throw new RuntimeException(getMessage("TaskWorkflowBrowseTableFrame.unknownTabType"));
            }
        }

        refresh();
        return sqlQuery;
    }

    private void replaceTable(String trackerQuery) {
        GroupDatasource<Tracker, UUID> trackerDs = new DsBuilder(getDsContext())
                .setJavaClass(Tracker.class)
                .setViewName("tracker-newTab")
                .setId("trackerDs")
                .buildGroupDatasource();
        trackerDs.setQuery(trackerQuery);
        this.remove(taskTable);
        GroupTable<Tracker> trackerTable = componentsFactory.createComponent(GroupTable.class);
        trackerTable.setId("trackerTable");
        trackerTable.setAlignment(Alignment.TOP_LEFT);
        trackerTable.setDatasource(trackerDs);
        trackerTable.setWidthFull();
        trackerTable.setHeightFull();
        trackerTable.setColumnCaption("incidentStatus", "Статус инцидента");
        trackerTable.removeColumn(trackerTable.getColumn("description"));
        this.add(trackerTable);
        trackerDs.refresh();
        trackerTable.groupByColumns("project", "incidentStatus");
        expand(trackerTable);
    }


    private void addTeamFiltration(StringBuilder query, Map<String, Object> params, boolean isIncidentsTab) {
        String property = isIncidentsTab ? "project" : "top";
        User currentUser = (User) params.get(USER);
        if (currentUser == null) {
            currentUser = userSession.getUser();
        }
        Performer performer = null;
        try {
            performer = dataManager.load(Performer.class).id(currentUser.getUuid()).view("user-with-roles").one();
        } catch (Exception e) {

        }
        if (performer != null) {
            List<Task> projects = new ArrayList<>();
            List<Team> teams = performer.getTeams();
            for (Team team : teams) {
                List<Task> teamProjects = team.getProjects();
                for (Task task : teamProjects) {
                    projects.add(task);
                }
            }
            query.append(" (e.").append(property).append(" is null or e.").append(property).append(".id in(")
                    .append(projects.stream().map(e -> "'" + e.getId() + "'").collect(Collectors.joining(",")))
                    .append("))");
            if (!isIncidentsTab) {
                query.append(" or (e.type ='").append(TaskType.fromId("project")).append("' and e.id in(")
                        .append(projects.stream().map(e -> "'" + e.getId() + "'").collect(Collectors.joining(",")))
                        .append("))");

            }
        } else {
            query.append("and e.").append(property).append(" is null");
        }
    }


    private void initTableBehaviour(Map<String, Object> params) {
        switch (tabType) {
            case NEW:
                initNewRecordsView();
                break;
            case WORKFLOW:
                initWorkflowView(params);
                break;
            default: {
                throw new RuntimeException("");
            }
        }
        //todo
        //update this part


    }

    private void initNewRecordsView() {
        Table<Tracker> trackerTable = (Table<Tracker>) getComponent("trackerTable");
        CreateAction createAction = new CreateAction(trackerTable) {
            @Override
            public String getWindowId() {
                return TrackerEdit.SCREEN_ID;
            }

            @Override
            public void actionPerform(Component component) {
                openEditor(dataManager.create(Tracker.class), WindowManager.OpenType.NEW_TAB).addCloseWithCommitListener(() -> {
                    trackerTable.getDatasource().refresh();
                });
            }
        };
        Button createButton = componentsFactory.createComponent(Button.class);
        createButton.setAction(createAction);

        RemoveAction removeAction = new RemoveAction(trackerTable, true, "removeTracker") {
            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Tracker> problems = trackerTable.getSelected();
                    if (!CollectionUtils.isEmpty(problems)) {
                        for (Tracker problem : problems) {
                            if (!IncidentStatus.NEW.equals(problem.getIncidentStatus())) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        Button removeButton = componentsFactory.createComponent(Button.class);
        removeButton.setAction(removeAction);
//        BaseAction runAction = new BaseAction("run") {
//            @Override
//            public String getCaption() {
//                return getMessage("TaskWorkflowBrowseTableFrame.startWorkflow");
//            }
//
//            @Override
//            public String getIcon() {
//                return CubaIcon.PAPER_PLANE.source();
//            }
//
//            @Override
//            public void actionPerform(Component component) {
//                final Set<Task> records = taskTable.getSelected();
//                if (!CollectionUtils.isEmpty(records)) {
//                    try {
//                        StringBuilder sb = new StringBuilder();
//                        for (Task tr : records) {
//                            String message;
//                            try {
//                                tr = dataManager.reload(tr, "Task-process");
//                                if (tr.getStatus() == null) {
//                                    if (/*tr.getPerformer() == null*/false) {
//                                        message = getMessage("TaskWorkflowBrowseTableFrame.choosePerformer");
//                                    } else {
//                                        workflowService.startWorkflow(tr, workflowService.determinateWorkflow(tr));
//                                        message = String.format(getMessage("TaskWorkflowBrowseTableFrame.workflowStarted"), tr.getId());
//                                    }
//                                } else {
//                                    message = String.format(getMessage("TaskWorkflowBrowseTableFrame.alreadyInProgress"), tr.getId());
//                                }
//                            } catch (WorkflowException e) {
//                                message = String.format(getMessage("TaskWorkflowBrowseTableFrame.workflowFailed"),
//                                        tr.getUuid(), e.getMessage() == null ? getMessage("TaskWorkflowBrowseTableFrame.notAvailable") : e.getMessage());
//                            }
//                            if (sb.length() > 0) {
//                                sb.append("\n");
//                            }
//                            sb.append(message);
//                        }
//                        String message = sb.toString();
//                        if (!StringUtils.isEmpty(message)) {
//                            MessageDialog.showText(getFrame(), message, false, true);
//                        }
//                    } finally {
//                        taskDs.refresh();
//                    }
//                }
//            }
//
//            @Override
//            public boolean isPermitted() {
//                if (super.isPermitted()) {
//                    Set<Task> problems = taskTable.getSelected();
//                    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(problems) && problems.size() > 0) {
//                        for (Task problem : problems) {
//                            if (problem.getStatus() != null /*|| problem.getPerformer() == null*/) {
//                                return false;
//                            }
//                        }
//                        return true;
//                    }
//                }
//                return false;
//            }
//        };
//        Button runButton = componentsFactory.createComponent(Button.class);
//        runButton.setAction(runAction);

        trackerTable.addAction(createAction);
        buttonsPanel.add(createButton);

        addEditAction(trackerTable);
        addRefreshAction(trackerTable);

        trackerTable.addAction(removeAction);
        buttonsPanel.add(removeButton);

//        trackerTable.addAction(runAction);
//        buttonsPanel.add(runButton);

        addExcelAction(trackerTable);
    }

    private void initWorkflowView(Map<String, Object> params) {
        //изменение + обновление + эксель
        addEditAction(taskTable);
        addRefreshAction(taskTable);
        addExcelAction(taskTable);

        //инициализировать расширения
        initWorkflowExtension(params);
    }

    private void initWorkflowExtension(Map<String, Object> params) {
        Stage stage = (Stage) params.get(STAGE);
        if (stage != null && TabType.WORKFLOW.equals(tabType)) {//this is not default tab, we must extend its view by stage behaviour
            try {
                workflowWebBean.extendBrowser(stage, this, Boolean.TRUE.equals(viewOnly));
            } catch (Exception e) {
                //log.error("Failed to extend browser screen", e);
                throw new RuntimeException(getMessage("TaskWorkflowBrowseTableFrame.errorOnScreenExtension"), e);
            }
        }
    }

    //refresh queries table
    public void refresh() {
        taskDs.refresh();


    }


    public void addEditAction(Table table) {
        EditAction editAction = EditAction.create(table, WindowManager.OpenType.NEW_TAB, TaskEdit.SCREEN_ID);
        Button editButton = componentsFactory.createComponent(Button.class);
        editButton.setAction(editAction);
        table.addAction(editAction);
        buttonsPanel.add(editButton);
    }

    public void addRefreshAction(Table table) {
        RefreshAction refreshAction = new RefreshAction(table);
        refreshAction.setShortcut("CTRL-R");
        Button refreshButton = componentsFactory.createComponent(Button.class);
        refreshButton.setAction(refreshAction);
        table.addAction(refreshAction);
        buttonsPanel.add(refreshButton);
    }

    public void addExcelAction(Table table) {
        ExcelAction excelAction = new ExcelAction(table);
        Button excelButton = componentsFactory.createComponent(Button.class);
        excelButton.setAction(excelAction);
        table.addAction(excelAction);
        buttonsPanel.add(excelButton);
    }


    /**
     * Очистка выделения у таблицы
     */
    public void clearSelection() {
        if (taskTable instanceof ExternalSelectionGroupTable) {
            Action action = taskTable.getAction("clearSelection");
            if (action != null)
                action.actionPerform(null);
        } else {
            taskTable.setSelected((Task) null);
        }
    }

}

