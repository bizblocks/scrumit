package com.company.scrumit.web.tracker.workflow.frame;

import com.company.scrumit.entity.Tracker;
import com.company.scrumit.web.tracker.TabType;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.groupstp.workflowstp.web.components.ExternalSelectionGroupTable;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.springframework.util.CollectionUtils;


import javax.inject.Inject;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TrackerWorkflowBrowseTableFrame extends AbstractFrame {

    public static final String SCREEN_ID = "tracker-workflow-table";
    public static final String TAB_TYPE = "tabType";
    public static final String VIEW_ONLY = "viewOnly";
    //public static final String STAGE = "stage";

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
    @WindowParam(name = TAB_TYPE, required = true)
    protected TabType tabType;

    @WindowParam(name = VIEW_ONLY, required = true)
    protected Boolean viewOnly;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initSqlQuery(params);
        initTableBehaviour(params);
    }


    private void initSqlQuery(Map<String, Object> params) {
        String sqlQuery = "select e from scrumit$Tracker e";
        trackerDs.setQuery(sqlQuery);
        trackerDs.refresh();
    }


    private void initTableBehaviour(Map<String, Object> params) {
        switch (tabType) {
            case NEW:
                initNewRecordsView();
                break;
            default: {
                throw new RuntimeException("");
            }
        }


    }


    private void initNewRecordsView() {
        BaseAction runAction = new BaseAction("run") {
            @Override
            public String getCaption() {
                return getMessage("sendToWork");
            }

            @Override
            public String getIcon() {
                return CubaIcon.OK.source();
            }

            @Override
            public void actionPerform(Component component) {
                final Set<Tracker> records = trackerTable.getSelected();
                if (!CollectionUtils.isEmpty(records)) {
                    //StringBuilder sb = new StringBuilder();
                    for (Tracker tr : records) {
                        //String message;
                        try {
                            tr = dataManager.reload(tr, "tracker-process");
                            if (tr.getStatus() == null) {
                                workflowService.startWorkflow(tr, workflowService.determinateWorkflow(tr));
                            }
                        } catch (WorkflowException e) {

                        }
                    }
                }
            }
        };
        Button runButton = componentsFactory.createComponent(Button.class);
        runButton.setAction(runAction);
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


