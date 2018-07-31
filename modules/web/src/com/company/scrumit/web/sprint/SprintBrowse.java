package com.company.scrumit.web.sprint;

import com.company.scrumit.entity.Sprint;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import com.haulmont.cuba.gui.components.GroupTable;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
}