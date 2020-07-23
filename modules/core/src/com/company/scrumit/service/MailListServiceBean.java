package com.company.scrumit.service;

import com.company.scrumit.utils.StringUtil;
import com.company.scrumit.entity.*;
import com.groupstp.mailreader.entity.dto.*;
import com.groupstp.mailreader.service.GmailService;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(MailListService.NAME)
public class MailListServiceBean implements MailListService {

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
    @Inject
    private GmailService gmailService;
    @Inject
    private TrackerService trackerService;


    @Override
    public void checkEmails() {
            List<ThreadDto> threadDtos = gmailService.receive();
        threadDtos.forEach(threadDto -> {
            Tracker tracker = trackerService.getTrackerByThreadId(threadDto.getId());
            if (PersistenceHelper.isNew(tracker)){
                String email = stringUtil.getEmailFromString(threadDto.getMessages().get(0).getRecipient());
                tracker.setThreadId(threadDto.getId());
                tracker.setThreadSize(threadDto.getMessages().size());
                tracker.setShortdesc(!StringUtils.isEmpty(threadDto.getMessages().get(0).getSubject()) ? threadDto.getMessages().get(0).getSubject() : "(без темы)");
                String content = threadDto.getMessages().get(0).getTextContent();


                content = content.replaceAll("<img src=[^<]+", "[файл находится во вложениях]");


                tracker.setDescription(content);
                tracker.setTrackerPriorityType(TrackerPriorityType.Current);
                tracker.setType(TicketsType.SUPPORT);
                Task task = metadata.create(Task.class);
                task.setDescription(content);
                task.setPriority(Priority.Middle);
                task.setShortdesc(!StringUtils.isEmpty(threadDto.getMessages().get(0).getSubject()) ? threadDto.getMessages().get(0).getSubject() : "(без темы)");
                tracker.setIncidentStatus(IncidentStatus.NEW);
                tracker.setInitiatorEmail(threadDto.getMessages().get(0).getFrom());

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
                List<FileDescriptor> attachments = new ArrayList<>();
                threadDto.getMessages().forEach(messageDto -> {
                    attachments.addAll(messageDto.getAttachments());
                });
                for (FileDescriptor fileDescriptor :attachments) {
                    fileDescriptor = dataManager.commit(fileDescriptor);
                    Files files = metadata.create(Files.class);
                    files.setDescription(fileDescriptor.getName());
                    files.setEntity(tracker.getUuid());
                    files.setFile(fileDescriptor);
                    dataManager.commit(files);
                }


                threadDto.getMessages().remove(0);
                StringBuilder sb = new StringBuilder(tracker.getDescription());
                threadDto.getMessages().forEach(messageDto -> {
                    sb.append("\n----------------------------------\n");
                    sb.append(messageDto.getFrom());
                    sb.append(": ");
                    sb.append(messageDto.getTextContent());
                });
                tracker = dataManager.reload(tracker,"tracker-description");
                tracker.setDescription(sb.toString());
                dataManager.commit(tracker);
            }
            else if (threadDto.getMessages().size() > tracker.getThreadSize()){
                tracker = dataManager.reload(tracker,"tracker-description");
                StringBuilder sb = new StringBuilder(tracker.getDescription());
                for (int i = tracker.getThreadSize(); i < threadDto.getMessages().size(); i++){
                    sb.append("\n----------------------------------\n");
                    sb.append(threadDto.getMessages().get(i).getFrom());
                    sb.append(": ");
                    sb.append(threadDto.getMessages().get(i).getTextContent());
                }
                tracker.setDescription(sb.toString());
                tracker.setThreadSize(threadDto.getMessages().size());
                dataManager.commit(tracker);
            }

            });
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