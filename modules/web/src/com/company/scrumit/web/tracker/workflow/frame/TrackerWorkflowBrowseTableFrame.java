package com.company.scrumit.web.tracker.workflow.frame;

import com.company.scrumit.entity.Tracker;
import com.company.scrumit.web.tracker.TabType;
import com.company.scrumit.web.tracker.TrackerEdit;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.StageType;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.groupstp.workflowstp.util.EqualsUtils;
import com.groupstp.workflowstp.web.components.ExternalSelectionGroupTable;
import com.groupstp.workflowstp.web.util.action.AlwaysActiveAction;
import com.groupstp.workflowstp.web.util.messagedialog.MessageDialog;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Scripting;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;


import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TrackerWorkflowBrowseTableFrame extends AbstractFrame {

    public static final String SCREEN_ID = "tracker-workflow-table";
    public static final String TAB_TYPE = "tabType";
    public static final String VIEW_ONLY = "viewOnly";
    public static final String STAGE = "stage";
    public static final String USER = "user";

    @Inject
    private CollectionDatasource<Tracker, UUID> trackerDs;
    @Inject
    private GroupTable<Tracker> trackerTable;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    private ButtonsPanel buttonsPanel;
    @Inject
    protected DataManager dataManager;
    @Inject
    private WorkflowService workflowService;
    @Inject
    protected Scripting scripting;
    @WindowParam(name = TAB_TYPE, required = true)
    protected TabType tabType;

    @WindowParam(name = VIEW_ONLY, required = true)
    protected Boolean viewOnly;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initFiltersId(params);
        initSqlQuery(params);
        initTableBehaviour(params);
        //tab opened for view only
        if (Boolean.TRUE.equals(viewOnly)) {
            trackerTable.setEditable(false);
            if (!org.apache.commons.collections4.CollectionUtils.isEmpty(trackerTable.getActions())) {
                for (Action action : trackerTable.getActions()) {
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

    private void initSqlQuery(Map<String, Object> params) {
        String sqlQuery = "select e from scrumit$Tracker e";
        switch (tabType) {
            case NEW:
                sqlQuery += " where e.workflow is null";
                break;
            case WORKFLOW:
                Stage stage = (Stage) params.get(STAGE);
                sqlQuery += " where e.stepName='" + stage.getName() + "'";
                break;
            default: {
                throw new RuntimeException(getMessage("trackerWorkflowBrowseTableFrame.unknownTabType"));
            }
        }
        trackerDs.setQuery(sqlQuery);
        refresh();
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


    }


    private void initWorkflowView(Map<String, Object> params) {
        EditAction editAction = new EditAction(trackerTable) {
            @Override
            public String getWindowId() {
                return TrackerEdit.SCREEN_ID;
            }

            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Tracker> problems = trackerTable.getSelected();
                    return !org.apache.commons.collections4.CollectionUtils.isEmpty(problems) && problems.size() == 1;
                }
                return false;
            }
        };
        Button editButton = componentsFactory.createComponent(Button.class);
        editButton.setAction(editAction);

        RefreshAction refreshAction = new RefreshAction(trackerTable);
        refreshAction.setShortcut("CTRL-R");
        Button refreshButton = componentsFactory.createComponent(Button.class);
        refreshButton.setAction(refreshAction);

        trackerTable.addAction(editAction);
        buttonsPanel.add(editButton);

        trackerTable.addAction(refreshAction);
        buttonsPanel.add(refreshButton);

        initWorkflowExtension(params);
    }

    private void initWorkflowExtension(Map<String, Object> params) {
        Stage stage = (Stage) params.get(STAGE);
        if (stage != null && TabType.WORKFLOW.equals(tabType)) {//this is not default tab, we must extend its view by stage behaviour
            if (EqualsUtils.equalAny(stage.getType(), StageType.USERS_INTERACTION, StageType.ARCHIVE)) {
                final String script = stage.getBrowseScreenGroovyScript();
                if (!StringUtils.isEmpty(script)) {
                    final Map<String, Object> binding = new HashMap<>();
                    binding.put("stage", stage);
                    binding.put("screen", this);
                    binding.put("viewOnly", Boolean.TRUE.equals(viewOnly));
                    try {
                        scripting.evaluateGroovy(script, binding);
                    } catch (Exception e) {
                        //log.error("Failed to evaluate browse screen groovy for stage {}({})", stage, stage.getId());
                        throw new RuntimeException(getMessage("trackerWorkflowBrowseTableFrame.errorOnScreenExtension"), e);
                    }
                }
            }
        }
    }


    //refresh queries table
    public void refresh() {
        trackerDs.refresh();
    }

    private void initNewRecordsView() {
        CreateAction createAction = new CreateAction(trackerTable) {
            @Override
            public String getWindowId() {
                return TrackerEdit.SCREEN_ID;
            }
        };
        Button createButton = componentsFactory.createComponent(Button.class);
        createButton.setAction(createAction);

        EditAction editAction = new EditAction(trackerTable) {
            @Override
            public String getWindowId() {
                return TrackerEdit.SCREEN_ID;
            }

            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Tracker> problems = trackerTable.getSelected();
                    return !org.apache.commons.collections4.CollectionUtils.isEmpty(problems) && problems.size() == 1;
                }
                return false;
            }
        };
        Button editButton = componentsFactory.createComponent(Button.class);
        editButton.setAction(editAction);


        RemoveAction removeAction = new RemoveAction(trackerTable) {
            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Tracker> problems = trackerTable.getSelected();
                    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(problems)) {
                        for (Tracker problem : problems) {
                            if (problem.getStatus() != null) {
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

        RefreshAction refreshAction = new RefreshAction(trackerTable);
        refreshAction.setShortcut("CTRL-R");
        Button refreshButton = componentsFactory.createComponent(Button.class);
        refreshButton.setAction(refreshAction);

        BaseAction runAction = new BaseAction("run") {
            @Override
            public String getCaption() {
                return getMessage("trackerWorkflowBrowseTableFrame.startWorkflow");
            }

            @Override
            public String getIcon() {
                return CubaIcon.PAPER_PLANE.source();
            }

            @Override
            public void actionPerform(Component component) {
                final Set<Tracker> records = trackerTable.getSelected();
                if (!CollectionUtils.isEmpty(records)) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        for (Tracker tr : records) {
                            String message;
                            try {
                                tr = dataManager.reload(tr, "tracker-process");
                                if (tr.getStatus() == null) {
                                    if (/*tr.getPerformer() == null*/false) {
                                        message = getMessage("trackerWorkflowBrowseTableFrame.choosePerformer");
                                    } else {
                                        workflowService.startWorkflow(tr, workflowService.determinateWorkflow(tr));
                                        message = String.format(getMessage("trackerWorkflowBrowseTableFrame.workflowStarted"), tr.getId());
                                    }
                                } else {
                                    message = String.format(getMessage("trackerWorkflowBrowseTableFrame.alreadyInProgress"), tr.getId());
                                }
                            } catch (WorkflowException e) {
                                message = String.format(getMessage("trackerWorkflowBrowseTableFrame.workflowFailed"),
                                        tr.getUuid(), e.getMessage() == null ? getMessage("trackerWorkflowBrowseTableFrame.notAvailable") : e.getMessage());
                            }
                            if (sb.length() > 0) {
                                sb.append("\n");
                            }
                            sb.append(message);
                        }
                        String message = sb.toString();
                        if (!StringUtils.isEmpty(message)) {
                            MessageDialog.showText(getFrame(), message, false, true);
                        }
                    } finally {
                        trackerDs.refresh();
                    }
                }
            }

            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Tracker> problems = trackerTable.getSelected();
                    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(problems) && problems.size() > 0) {
                        for (Tracker problem : problems) {
                            if (problem.getStatus() != null /*|| problem.getPerformer() == null*/) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        Button runButton = componentsFactory.createComponent(Button.class);
        runButton.setAction(runAction);


        trackerTable.addAction(createAction);
        buttonsPanel.add(createButton);
        trackerTable.addAction(editAction);
        buttonsPanel.add(editButton);
        trackerTable.addAction(removeAction);
        buttonsPanel.add(removeButton);
        trackerTable.addAction(refreshAction);
        buttonsPanel.add(refreshButton);
        trackerTable.addAction(runAction);
        buttonsPanel.add(runButton);
    }

    /**
     * Очистка выделения у таблицы
     */
    public void clearSelection() {
        if (trackerTable instanceof ExternalSelectionGroupTable) {
            Action action = trackerTable.getAction("clearSelection");
            if (action != null)
                action.actionPerform(null);
        } else {
            trackerTable.setSelected((Tracker) null);
        }
    }

}


