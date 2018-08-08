package com.company.scrumit.web.sprint;

import com.company.scrumit.entity.Sprint;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.components.actions.ExcludeAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SprintBrowse extends EntityCombinedScreen {

    @Inject
    private GroupTable<Sprint> table;

    private void exportToSmartsheet(Collection sel) {
        if(sel.size()==0)
            return;
        long taskId = (long) ((KeyValueEntity)sel.toArray()[0]).getId();
        Sprint current = table.getSingleSelected();
        if(current==null)
            return;
        long sheetId = current.getTeam().getSmartsheetId();
        Map<String, Object> params = new HashMap<>();
        params.put("sheetId", sheetId);
        params.put("taskId", taskId);
        params.put("sprint", current);
        openWindow("SmartsheetExport", WindowManager.OpenType.THIS_TAB, params);
    }

    public void onExport(Component source) {
        Sprint current = table.getSingleSelected();
        if(current==null) {
            showNotification("Select sprint first", NotificationType.WARNING);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("sheetId", table.getSingleSelected().getTeam().getSmartsheetId());
        //openLookup("SmartsheetExport", SprintBrowse::exportToSmartsheet, WindowManager.OpenType.THIS_TAB, params);
        openLookup("SmartsheetExport", this::exportToSmartsheet, WindowManager.OpenType.THIS_TAB, params);
    }

    @Inject
    private HierarchicalDatasource<Task, UUID> tasksDs1;


    @Inject
    private Datasource<Sprint> sprintDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        tasksDs1.addCollectionChangeListener(e -> {
            if(!e.getOperation().equals(CollectionDatasource.Operation.ADD))
                return;
            sprintDs.getItem().getTasks().addAll(e.getItems());
        });
    }

    /**
     * Hook to be implemented in subclasses. <br>
     * Called by the framework after the screen is fully initialized and opened. <br>
     * Override this method and put custom initialization logic here.
     */
    @Override
    public void ready() {
        super.ready();
        initEditComponents(false);
    }

    @Inject
    private Button btnCreateTask;

    @Inject
    private TreeTable<Task> tasks;

    @Inject
    private Button btnExcludeTasks;

    @Named("tasks.add")
    private AddAction tasksAdd;

    @Named("tasks.exclude")
    private ExcludeAction tasksExclude;

    @Named("tasks.editTask")
    private Action tasksEditTask;


    /**
     * Initializes edit controls, depending on if they should be enabled or disabled.
     *
     * @param enabled if true - enables edit controls and disables controls on the left side of the splitter
     *                if false - vice versa
     */
    @Override
    protected void initEditComponents(boolean enabled) {
        super.initEditComponents(enabled);
        tasksAdd.setEnabled(enabled);
        tasksExclude.setEnabled(enabled);
    }

    /**
     * Method that is invoked by clicking Ok button after editing an existing or creating a new record.
     */
    @Override
    public void save() {
        sprintDs.commit();
        super.save();
    }

    public void onEditTask(Component source) {
        openEditor(tasksDs1.getItem(), WindowManager.OpenType.THIS_TAB);
    }
}