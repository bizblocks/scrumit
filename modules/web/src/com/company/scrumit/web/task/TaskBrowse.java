package com.company.scrumit.web.task;

import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.gui.components.AbstractLookup;
import org.springframework.context.event.EventListener;

public class TaskBrowse extends AbstractLookup {

    @EventListener
    public void onUiEvent(UiEvent event) {
        if ("taskRefresh".equals(event.getSource())) {
            getDsContext().refresh();
        }
    }

}