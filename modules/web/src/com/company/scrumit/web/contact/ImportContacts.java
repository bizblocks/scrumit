package com.company.scrumit.web.contact;

import com.company.scrumit.entity.City;
import com.company.scrumit.entity.Contact;
import com.company.scrumit.entity.Speciality;
import com.company.scrumit.service.ImportCSVService;
import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.core.app.PersistenceManagerService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.ResizableTextArea;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ImportContacts extends AbstractWindow {

    @Inject
    private ImportCSVService importCSVService;

    @Inject
    private ResizableTextArea csv;


    public void onBtnImportClick() throws IOException {
        List<HashMap<String,String>> data = importCSVService.parseCSV(csv.getRawValue());
        importCSVService.importContacts(data);
        showNotification("Import finished");
        UiEvent.push("contactRefresh");
    }
}