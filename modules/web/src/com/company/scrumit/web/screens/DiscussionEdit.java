package com.company.scrumit.web.screens;

import com.company.scrumit.entity.Discussion;
import com.company.scrumit.entity.Message;
import com.company.scrumit.entity.Tracker;
import com.company.scrumit.service.DaoService;
import com.company.scrumit.service.TrackerService;
import com.company.scrumit.utils.StringUtil;
import com.groupstp.mailreader.entity.ConnectionData;
import com.groupstp.mailreader.entity.dto.MessageDto;
import com.groupstp.mailreader.service.GmailService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DiscussionEdit extends AbstractWindow {


    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private RichTextArea inputField;
    @Inject
    private VBoxLayout dialogSpace;
    @Inject
    private UserSession userSession;
    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;

    Discussion discussion;

    List<Message> messages;

    Tracker tracker;

    @Inject
    private TrackerService trackerService;


    @Override
    public void init(Map<String, Object> params) {

        tracker = dataManager.reload((Tracker) params.get("tracker"),"tracker-sendMessage");

       discussion = dataManager.reload(tracker,"_full").getDiscussion();

        if (discussion==null){
            discussion = metadata.create(Discussion.class);
            discussion.setInitiator(userSession.getCurrentOrSubstitutedUser());
            discussion.setTracker((Tracker) params.get("tracker"));
            discussion.setMessages(new ArrayList<>());
            discussion = dataManager.commit(discussion,"discussion-full");

        }else {
            discussion=dataManager.reload(discussion, "discussion-full");
        }
        messages = discussion.getMessages();
        messages.sort(Comparator.comparing(message -> message.getReceiptTime()));
        for (Message message: messages){
            createTextField(message);

        }
        dialogSpace.setHeightFull();
        dialogSpace.setWidthFull();

    }

    private void createTextField(Message message) {
        RichTextArea textField = componentsFactory.createComponent(RichTextArea.class);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.isBlank(message.getFrom())? getMessage("unknown_sender") +":\n" : message.getFrom()+ ":\n");
        stringBuilder.append(message.getText());
        textField.setValue(stringBuilder.toString());
            textField.setAlignment(Alignment.BOTTOM_LEFT);


        textField.setEditable(false);
        textField.setHeightAuto();
        textField.setWidthFull();
        dialogSpace.add(textField);
    }

    public void onSendBtnClick() throws GeneralSecurityException, MessagingException, IOException {
        Message message = trackerService.sendReplyMessageinTrackerDiscussion(tracker, inputField.getValue());
        createTextField(message);
    }
}