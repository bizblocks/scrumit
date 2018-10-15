package com.company.scrumit.web.entity;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import org.springframework.context.ApplicationEvent;

public class UiEvent extends ApplicationEvent implements com.haulmont.cuba.gui.events.UiEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public UiEvent(Object source) {
        super(source);
    }

    public static void push(String message) {
        AppBeans.get(Events.class).publish(new UiEvent(message));
    }

}
