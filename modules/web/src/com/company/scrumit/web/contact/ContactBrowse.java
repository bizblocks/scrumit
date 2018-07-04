package com.company.scrumit.web.contact;

import com.company.scrumit.entity.Contact;
import com.company.scrumit.entity.Performer;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;

import javax.inject.Inject;

public class ContactBrowse extends AbstractLookup {

    public void onImportClick() {
        openWindow("importContacts", WindowManager.OpenType.DIALOG);
    }

    @Inject
    private GroupTable<Contact> contactsTable;

    public void onBtnCreatePerformerClick() {
        if(contactsTable.getSelected().size()==0)
        {
            showNotification(getMessage("Select contacts first"), NotificationType.ERROR);
            return;
        }
        contactsTable.getSelected().forEach(contact -> createPerformer(contact));
    }

    public void createPerformer(Contact contact)
    {
        Performer performer = new Performer();
        performer.setContact(contact);
        String email = contact.getEmail();
        performer.setLogin(email);
        performer.setEmail(email);
        performer.setName(contact.getFio());
        openEditor(performer, WindowManager.OpenType.DIALOG);
    }
}