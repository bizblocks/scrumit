package com.company.scrumit.web.meeting;

import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.gui.components.AbstractLookup;
import org.springframework.context.event.EventListener;

public class MeetingBrowse extends AbstractLookup {

    @EventListener
    public void onUiEvent(UiEvent event) {
        if ("meetingRefresh".equals(event.getSource())) {
            getDsContext().refresh();
        }
    }

}