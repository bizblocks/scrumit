package com.company.scrumit.web.screens;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.screen.CloseAction;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;

@UiController("scrumit_CommentOnReturnScreen")
@UiDescriptor("comment-on-return-screen.xml")
public class CommentOnReturnScreen extends AbstractWindow {

    String comment;

    @Inject
    private TextArea<String> textArea;

    public void onButtonClick() {
        comment = textArea.getRawValue();
        if (comment == null || comment.trim().isEmpty()) {
            showNotification(messages.getMainMessage("comment_is_required"));
        } else {
            close("ok");

        }
    }

    public String getComment() {
        return comment;
    }
}