package com.company.scrumit.web.contact;

import com.company.scrumit.entity.ContactsSpeciality;
import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.scrumit.entity.Contact;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.UUID;

public class ContactEdit extends AbstractEditor<Contact> {

    @Inject
    private DataManager dataManager;

    @Inject
    private DataGrid<ContactsSpeciality> specs;

    @Inject
    private CollectionDatasource<ContactsSpeciality, UUID> specialitiesDs;

    public void onAddCategoryClick() {
        ContactsSpeciality cs = new ContactsSpeciality();
        cs.setContact(this.getItem());
        specialitiesDs.addItem(cs);
        specs.edit(cs);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        UiEvent.push("contactRefresh");
        return super.postCommit(committed, close);
    }
}