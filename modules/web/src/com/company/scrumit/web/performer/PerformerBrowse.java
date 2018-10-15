package com.company.scrumit.web.performer;

import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import org.springframework.context.event.EventListener;

public class PerformerBrowse extends EntityCombinedScreen {

    @EventListener
    public void onUiEvent(UiEvent event) {
        if ("perfomerRefresh".equals(event.getSource())) {
            getDsContext().refresh();
        }
    }

}