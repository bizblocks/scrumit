package com.company.scrumit.web.performer;

import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.scrumit.entity.Performer;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.PasswordField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import javax.inject.Named;

public class PerformerEdit extends AbstractEditor<Performer> {

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private ComponentsFactory componentsFactory;

    /**
     * Hook to be implemented in subclasses. <br>
     * Called by the framework after the screen is fully initialized and opened. <br>
     * Override this method and put custom initialization logic here.
     */
    @Override
    public void ready() {
        super.ready();
        FieldGroup.FieldConfig fc = fieldGroup.createField("password");
        fc.setProperty("password");
        fc.setCaption(getMessage("Password"));
        fc.setComponent(componentsFactory.createComponent(PasswordField.class));
        fieldGroup.addField(fc, 0, 1);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        UiEvent.push("perfomerRefresh");
        return super.postCommit(committed, close);
    }
}