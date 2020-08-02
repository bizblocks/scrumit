package com.company.scrumit.service;

import com.company.scrumit.entity.Message;
import com.company.scrumit.utils.StringUtil;
import com.company.scrumit.entity.IncidentStatus;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Tracker;
import com.groupstp.mailreader.entity.ConnectionData;
import com.groupstp.mailreader.entity.dto.MessageDto;
import com.groupstp.mailreader.service.GmailService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.global.UserSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service(TrackerService.NAME)
public class TrackerServiceBean implements TrackerService {

    @Inject
    private DataManager dataManager;
    @Inject
    private StringUtil stringUtil;
    @Inject
    private Metadata metadata;
    @Inject
    private DaoService daoService;
    @Inject
    private UserSessionSource userSession;
    @Inject
    private GmailService gmailService;

    @Override
    public IncidentStatus updateIncidentStatus(Tracker incident) {
        incident = dataManager.reload(incident,"tracker-newTab");
        IncidentStatus result = null;
        Boolean isNew = Boolean.FALSE;
        Boolean isDone = Boolean.FALSE;
        Boolean isInWork = Boolean.FALSE;
        if (!incident.getTask().isEmpty()) {
            for (Task task : incident.getTask()) {
                task = dataManager.reload(task, "task-tree");
                result = checkStatusByChildrenRecursive(isNew, isDone, isInWork, task);
                incident.setIncidentStatus(result);
            }
        }else {
            result = IncidentStatus.NEW;
            incident.setIncidentStatus(result);
        }
        dataManager.commit(incident);
        return result;
    }

    public IncidentStatus checkStatusByChildrenRecursive(Boolean isNew, Boolean isDone, Boolean isInWork, Task parent) {
        if (!parent.getChildren().isEmpty()) {
            for (Task task : parent.getChildren()) {
                task = dataManager.reload(task, "task-tree");
                return checkStatusByChildrenRecursive(isNew, isDone, isInWork, task);
            }
        }
        if (parent.getStatus() == null) {
            return IncidentStatus.NEW;
        } else {
            switch (parent.getStatus()) {
                case DONE:
                    isDone = Boolean.TRUE;
                    break;
                case IN_PROGRESS:
                    isInWork =  Boolean.TRUE;
                    break;
                case FAILED:
                    isDone =  Boolean.TRUE;
                    break;
                default:
                    isNew =  Boolean.TRUE;
            }
            if ((isDone && isNew) || isInWork) {
               return IncidentStatus.IN_WORK;
            } else if (isNew) {
                return IncidentStatus.NEW;
            } else return IncidentStatus.DONE;
        }

    }

    @Override
    public String getEmailFormString(String source){
        return stringUtil.getEmailFromString(source);
    }

    @Override
    public List<String> getAllThreadIds(){
        return dataManager.loadValue("select f.threadId from scrumit$Tracker f",String.class)
                .list();
    }
    @Override
    public Tracker getTrackerByThreadId(String threadId){
        return dataManager.load(Tracker.class)
                .query("select f from scrumit$Tracker f where f.threadId = :id")
                .parameter("id", threadId)
                .view("tracker-with-threadSize")
                .optional().orElse(metadata.create(Tracker.class));
    }
    @Override
    public Message sendReplyMessageinTrackerDiscussion(Tracker tracker, String text) throws GeneralSecurityException, MessagingException, IOException {
        tracker = dataManager.reload(tracker ,"tracker-sendMessage");
        List<Message> messages = tracker.getDiscussion().getMessages();
        messages.sort(Comparator.comparing(message -> message.getReceiptTime()));
        ConnectionData connectionData = daoService.findFirstConnectionDataByProject(tracker.getProject());
        if (connectionData == null){
            return null;
        }

        String to;
        String inReplyTo;
        String references;
        if (messages.isEmpty()){
            to = stringUtil.getEmailFromString(tracker.getInitiatorEmail());
            inReplyTo = tracker.getInitialMessageId();
            references = tracker.getInitialMessageId();
        }
        else {
            to =stringUtil.getEmailFromString(messages.get(messages.size() - 1).getFrom());
            inReplyTo = messages.get(messages.size() -1 ).getExtId();
            references = messages.get(messages.size() -1 ).getReferences() +" " + inReplyTo;
        }

        Message message= metadata.create(Message.class);
        message.setText(text);
        message.setAutor(userSession.getUserSession().getCurrentOrSubstitutedUser());
        message.setDiscussion(tracker.getDiscussion());
        message.setFrom(userSession.getUserSession().getCurrentOrSubstitutedUser().getEmail());
        message.setReferences(references);
        message.setInReplyTo(inReplyTo);




        MessageDto msg = gmailService.sendReplyMessage(connectionData,
                to,
                tracker.getShortdesc(),
                message.getText(),
                inReplyTo,
                tracker.getThreadId(),
                references);
        tracker.setThreadSize(tracker.getThreadSize() +1);
        message.setExtId(msg.getMessageExtId());
        message.setReceiptTime(msg.getReceiptTime());
        message.setFrom(msg.getFrom());
        dataManager.commit(tracker);
        return message=dataManager.commit(message);
    }
}