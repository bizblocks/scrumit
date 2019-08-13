package com.company.scrumit.web.incomingmessage;

import com.company.scrumit.entity.IncomingMessage;
import com.company.scrumit.service.MailService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import java.util.UUID;

public class IncomingMessageBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<IncomingMessage, UUID> incomingMessageDs;

    @Inject
    private DataManager dataManager;

    @Inject
    private MailService mailService;

    public void onReadLastMessageBtnClick() throws Exception {
        IncomingMessage incomingMessage = mailService.GetLastMessage();

        dataManager.commit(incomingMessage);

        incomingMessageDs.refresh();

        showNotification("Готово", NotificationType.TRAY);
    }
}