package com.company.scrumit.web.screens;

import com.company.scrumit.entity.Discussion;
import com.company.scrumit.entity.Message;
import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;

import javax.inject.Inject;
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


    @Override
    public void init(Map<String, Object> params) {

       Tracker tracker = (Tracker) params.get("tracker");

       discussion = dataManager.reload(tracker,"_full").getDiscussion();

        if (discussion==null){
            discussion = metadata.create(Discussion.class);
            discussion.setInitiator(userSession.getCurrentOrSubstitutedUser());
            discussion.setTracker((Tracker) params.get("tracker"));
            discussion = dataManager.commit(discussion,"discussion-full");

        }else {
            discussion=dataManager.reload(discussion, "discussion-full");
        }

        for (Message message: discussion.getMessages()){
            createTextField(message);

        }
        dialogSpace.setHeightFull();
        dialogSpace.setWidthFull();

    }

    private void createTextField(Message message) {
        TextField textField = componentsFactory.createComponent(TextField.class);
        textField.setValue(message.getText());
        if (!userSession.getCurrentOrSubstitutedUser().equals(message.getAutor())){
            textField.setAlignment(Alignment.BOTTOM_LEFT);

        }else {
            textField.setAlignment(Alignment.BOTTOM_RIGHT);
        }
        textField.setEditable(false);
        dialogSpace.add(textField);
    }

    public void onSendBtnClick() {
        Message message= metadata.create(Message.class);
        message.setText(inputField.getValue());
        message.setAutor(userSession.getCurrentOrSubstitutedUser());
        message.setDiscussion(discussion);
        message=dataManager.commit(message);

        createTextField(message);
    }
}