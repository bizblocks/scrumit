package com.company.scrumit.web.tracker;

import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;


import javax.inject.Inject;
import java.util.Map;

public class TrackerBrowse extends AbstractLookup {
    @Inject
    private CheckBox checkSelect;

    @Inject
    private GroupTable<Tracker> trackersTable;

    /**
     * Called by the framework after creation of all components and before showing the screen.
     * <br> Override this method and put initialization logic here.
     *
     * @param params parameters passed from caller's code, usually from
     *               {@link #openWindow(String, WindowManager.OpenType)} and similar methods, or set in
     *               {@code screens.xml} for this registered screen
     */
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        checkSelect.addValueChangeListener(e -> trackersTable.setTextSelectionEnabled((Boolean) e.getValue()));
    }
}