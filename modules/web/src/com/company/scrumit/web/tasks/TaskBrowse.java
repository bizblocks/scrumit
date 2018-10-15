package com.company.scrumit.web.tasks;

import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import org.springframework.context.event.EventListener;

public class TaskBrowse extends EntityCombinedScreen {

    @EventListener
    public void onUiEvent(UiEvent event) {
        if ("taskRefresh".equals(event.getSource())) {
            getDsContext().refresh();
        }
    }

}