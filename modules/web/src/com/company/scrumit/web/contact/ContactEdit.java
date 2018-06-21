package com.company.scrumit.web.contact;

import com.company.scrumit.entity.ContactSpeciality;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.scrumit.entity.Contact;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.UUID;

public class ContactEdit extends AbstractEditor<Contact> {

    @Inject
    private DataManager dataManager;

    @Inject
    private DataGrid<ContactSpeciality> specs;

    @Inject
    private CollectionDatasource<ContactSpeciality, UUID> specialitiesDs;

    public void onAddCategoryClick() {
        ContactSpeciality cs = new ContactSpeciality();
        cs.setContact(this.getItem());
        specialitiesDs.addItem(cs);
        specs.edit(cs);
    }
}