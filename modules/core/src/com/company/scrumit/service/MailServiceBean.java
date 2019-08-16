package com.company.scrumit.service;

import com.company.scrumit.config.MailConfig;
import com.company.scrumit.entity.*;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(MailService.NAME)
public class MailServiceBean implements MailService {

    @Inject
    protected MailConfig mailConfig;

    @Inject
    private DataManager dataManager;

    @Override
    public void CreateNewIncomingMessages(ServerType serverType) throws Exception {
        List<IncomingMessage> newMessages = GetNewMessages(ServerType.IMAP);
        for (IncomingMessage message : newMessages
        ) {
            dataManager.commit(message);
            //обновление параметра - дата и время последнего сохраненного письма
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(message.getSend_date());
            mailConfig.setLastMessageDate(date);
        }
    }

    private List<IncomingMessage> GetNewMessages(ServerType serverType) throws Exception {

        List<IncomingMessage> newIncomingMessages = new ArrayList<>();

        Properties props = new Properties();

        props.put("mail.store.protocol", serverType.toString().toLowerCase());
        if (mailConfig.getSsl()) {
            props.put("mail.imap.port", "993");
            props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.imap.socketFactory.fallback", "false");
            props.setProperty("mail.imap.socketFactory.port", "993");
        }

        Session session = Session.getInstance(props);
        Store store = session.getStore();

        store.connect(mailConfig.getServer(), mailConfig.getUser(), mailConfig.getPassword());

        Folder inbox = store.getFolder("INBOX");

        inbox.open(Folder.READ_ONLY);

        //так поиск осуществляется на сервере IMAP (больше даты, указанной в параметре)
        Message[] messages = inbox.search(new SentDateTerm(ComparisonTerm.GE, mailConfig.getLastMessageDate()));
        //так поиск осуществляется на клиенте (перед этим все сообщения скачиваются)
//        Message[] messages = inbox.search(new SearchTerm() {
//            @Override
//            public boolean match(Message msg) {
//                try {
//                    //if(msg.getHeader("Message-ID")[0].equals(messages_Id.get(0).getValue("message_id")))
//                    if (msg.getSentDate().after(mailConfig.getLastMessageDate()))
//                        return true;
//                } catch (MessagingException e) {
//                    e.printStackTrace();
//                }
//                return false;
//            }
//        });


        for (int i = 0; i < messages.length; i++) {
            if (i > 10)
                break;

            Message message = messages[i];

            Object content = message.getContent();
            //Content body
            String body;
            //HashMap<String, byte[]> mapBytes = new HashMap<>();
            //List<byte[]> mapBytes = new ArrayList<>();
            if (content instanceof Multipart) {
                StringBuilder messageContent = new StringBuilder();

                Multipart multipart = (Multipart) content;
                for (int j = 0; j < multipart.getCount(); j++) {
                    Part part = multipart.getBodyPart(j);
                    //ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    //ObjectOutputStream oos = new ObjectOutputStream(bos);
                    if (part.isMimeType("text/plain")) {
                        messageContent.append(part.getContent().toString());
                    } else {
                        //oos.writeObject(part.getContent());
                        //oos.flush();
                        //mapBytes.add(bos.toByteArray());
                    }
                }
                body = messageContent.toString();
            } else
                body = content.toString();

            //recipients
            Address[] addresses = message.getAllRecipients();
            StringBuilder recipientsBuilder = new StringBuilder();
            for (Address address : addresses
            ) {
                recipientsBuilder.append(((InternetAddress) address).getAddress())
                        .append("; ");
            }

            IncomingMessage im = new IncomingMessage();
            im.setSender(((InternetAddress) (messages[i].getFrom()[0])).getAddress());
            im.setRecipients(recipientsBuilder.toString());
            im.setSubject(messages[i].getSubject());
            im.setBody(body);
            im.setSend_date(messages[i].getSentDate());
            im.setMessage_id(messages[i].getHeader("Message-ID")[0]);

            newIncomingMessages.add(im);
        }

        return newIncomingMessages;
    }

    @Override
    public void CreateNewTrackerFromMail(IncomingMessage incomingMessage, String shortDescription, Task task, TrackerPriorityType priority, TicketsType ticketsType) {
        Tracker tracker = new Tracker();
        tracker.setShortdesc(shortDescription);
        tracker.setDescription(incomingMessage.getBody());
        tracker.setProject(task);
        tracker.setTrackerPriorityType(priority);
        tracker.setType(ticketsType);
        dataManager.commit(tracker);
    }
}