package com.company.scrumit.web.incomingmessage;

import com.company.scrumit.entity.IncomingMessage;
import com.company.scrumit.entity.MailType;
import com.company.scrumit.service.MailService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import com.haulmont.cuba.gui.components.Component;

public class IncomingMessageBrowse extends AbstractLookup {

    @Inject
    private CollectionDatasource<IncomingMessage, UUID> incomingMessagesDs;

    @Inject
    private DataManager dataManager;

    @Inject
    private MailService mailService;

    public void readLastMessageBtn() throws Exception {
//        List<IncomingMessage> newMessages = mailService.GetNewMessages(MailService.ServerType.IMAP);
//        for (IncomingMessage message : newMessages
//        ) {
//            dataManager.commit(message);
//        }
        mailService.CreateNewIncomingMessages(MailService.ServerType.IMAP);
        incomingMessagesDs.refresh();

//        if (newMessages.size() == 0)
//            showNotification("Новых сообщений нет", NotificationType.TRAY);
//        else
//            showNotification("Добавлено новых сообщений: " + newMessages.size(), NotificationType.TRAY);
    }

    public void onMarkAsRequest(Component source) {
        IncomingMessage incomingMessage = incomingMessagesDs.getItem();
        if (incomingMessage == null) {
            showNotification("Выберите сообщение", NotificationType.ERROR);
            return;
        }
        incomingMessage.setType(MailType.Request);
        dataManager.commit(incomingMessage);
        incomingMessagesDs.refresh();
    }

    public void onMarkAsSpam(Component source) {
        IncomingMessage incomingMessage = incomingMessagesDs.getItem();
        if (incomingMessage == null) {
            showNotification("Выберите сообщение", NotificationType.ERROR);
            return;
        }
        incomingMessage.setType(MailType.Spam);
        dataManager.commit(incomingMessage);
        incomingMessagesDs.refresh();
    }

    public void onCreateNewTrackerClick() {
    }
}