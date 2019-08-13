package com.company.scrumit.service;

import com.company.scrumit.entity.IncomingMessage;
import com.haulmont.cuba.core.app.EmailService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.*;
import java.io.IOException;
import java.util.Properties;

@Service(MailService.NAME)
public class MailServiceBean implements MailService {
    @Inject
    private EmailService emailService;

    @Override
    public IncomingMessage GetLastMessage() throws Exception {
        final String user = "";
        final String pass = "";
        final String host = "";

        Properties props = new Properties();

        props.put("mail.store.protocol", "imap");
        Session session = Session.getInstance(props);
        Store store = session.getStore();

        store.connect(host, user, pass);

        Folder inbox = store.getFolder("INBOX");

        inbox.open(Folder.READ_ONLY);

        Message m = inbox.getMessage(inbox.getMessageCount());
        Multipart mp = (Multipart) m.getContent();
        BodyPart bp = mp.getBodyPart(0);

        IncomingMessage im = new IncomingMessage();
        im.setBody(bp.getContent().toString());

        return im;
    }
}