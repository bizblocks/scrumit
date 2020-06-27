package com.company.scrumit.service;

import com.company.scrumit.utils.StringUtil;
import com.company.scrumit.entity.*;
import com.groupstp.mailreader.entity.ResultMessage;
import com.groupstp.mailreader.service.ReceiveEmailsService;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(MailListService.NAME)
public class MailListServiceBean implements MailListService {
    @Inject
    private ReceiveEmailsService receiveEmailsService;
    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;
    @Inject
    private ProjectIdentificatorService projectIdentificatorService;
    @Inject
    private EmailService emailService;
    @Inject
    private StringUtil stringUtil;

    @Override
    public void checkEmails() {
        try {
            List<ResultMessage> resultMessages = receiveEmailsService.receive();
            resultMessages.forEach(resultMessage -> {
               String email = stringUtil.getEmailFromString(resultMessage.getRecipient());
                Tracker tracker = metadata.create(Tracker.class);
                tracker.setShortdesc(resultMessage.getSubject());
                String content = resultMessage.getTextContent();


                    content = content.replaceAll("<img src=[^<]+", "[файл находится во вложениях]");


                tracker.setDescription(content);
                tracker.setTrackerPriorityType(TrackerPriorityType.Current);
                tracker.setType(TicketsType.SUPPORT);
                Task task = metadata.create(Task.class);
                task.setDescription(content);
                task.setPriority(Priority.Middle);
                task.setShortdesc(resultMessage.getSubject());
                tracker.setIncidentStatus(IncidentStatus.NEW);
                tracker.setInitiatorEmail(resultMessage.getFrom());

                if (email!= null) {
                    ExtConnectionData connectionData = dataManager.load(ExtConnectionData.class)
                            .query("select f from scrumit_ExtConnectionData f where f.username = :email")
                            .parameter("email", email)
                            .view("extConnectionData-full")
                            .one();
                    tracker.setProject(connectionData.getProject());
                    tracker.setNumber(projectIdentificatorService.generateTrackerNumber(tracker));
                    task.setTop(connectionData.getProject());
                    task.setTask(connectionData.getProject());
                    TaskClass taskClass = dataManager.load(TaskClass.class)
                            .query("select f from scrumit_TaskClass f where f.name = :name")
                            .parameter("name", "support")
                            .view("taskClass-full")
                            .one();
                    task.setTaskClass(taskClass);

                }
                task = dataManager.commit(task);
                tracker.setTask(Arrays.asList(task));
                task.setParentBug(tracker);
                dataManager.commit(task);
                for (FileDescriptor fileDescriptor : resultMessage.getAttachments()) {
                    fileDescriptor = dataManager.commit(fileDescriptor);
                    Files files = metadata.create(Files.class);
                    files.setDescription(fileDescriptor.getName());
                    files.setEntity(tracker.getUuid());
                    files.setFile(fileDescriptor);
                    dataManager.commit(files);
                }

            });
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sendEmail(String to, String subject,String templatePath, Map<String, Serializable> params){
        EmailInfo emailInfo = new EmailInfo(
                to,
                subject,
                null,
                templatePath,
                params
        );
        emailService.sendEmailAsync(emailInfo);
    }
}