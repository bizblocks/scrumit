package com.company.scrumit.web.contact;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;

public class ContactBrowse extends AbstractLookup {

    public void onImportClick() {
        openWindow("importContacts", WindowManager.OpenType.DIALOG);
    }
}