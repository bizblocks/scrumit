package com.company.scrumit.entity;

import com.groupstp.workflowstp.entity.WorkflowEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.entity.WorkflowEntityStatus;

@NamePattern("%s|shortdesc")
@Table(name = "SCRUMIT_TRACKER")
@Entity(name = "scrumit$Tracker")
public class Tracker extends StandardEntity implements WorkflowEntity<UUID> {
    private static final long serialVersionUID = -8847125133735817612L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected Task project;

    @Column(name = "STEP_NAME")
    protected String stepName;


    @Column(name = "STATUS_WORK_FLOW")
    protected Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKFLOW_ID")
    protected Workflow workflow;

    @Lob
    @Column(name = "TESTING_PLAN")
    protected String testingPlan;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "open"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMER_ID")
    protected Performer performer;

    @NotNull
    @Column(name = "SHORTDESC", nullable = false, length = 100)
    protected String shortdesc;


    @Column(name = "STATUS")
    protected String statusOld;

    @Column(name = "TRACKER_PRIORITY_TYPE")
    protected String trackerPriorityType;

    @Column(name = "TYPE_")
    protected String type;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @OneToMany(mappedBy = "parentBug")
    protected List<Task> task;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILES_ID")
    @OnDelete(DeletePolicy.CASCADE)
    protected FileDescriptor files;
    public void setStatus(WorkflowEntityStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public WorkflowEntityStatus getStatus() {
        return status == null ? null : WorkflowEntityStatus.fromId(status);
    }


    public void setStatusOld(Status statusOld) {
        this.statusOld = statusOld == null ? null : statusOld.getId();
    }

    public Status getStatusOld() {
        return statusOld == null ? null : Status.fromId(statusOld);
    }











    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }





    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Workflow getWorkflow() {
        return workflow;
    }


    public void setTestingPlan(String testingPlan) {
        this.testingPlan = testingPlan;
    }

    public String getTestingPlan() {
        return testingPlan;
    }


    public void setPerformer(Performer performer) {
        this.performer = performer;
    }

    public Performer getPerformer() {
        return performer;
    }


    public FileDescriptor getFiles() {
        return files;
    }

    public void setFiles(FileDescriptor files) {
        this.files = files;
    }


    public Task getProject() {
        return project;
    }

    public void setProject(Task project) {
        this.project = project;
    }


    public List<Task> getTask() {
        return task;
    }

    public void setTask(List<Task> task) {
        this.task = task;
    }






    public void setTrackerPriorityType(TrackerPriorityType trackerPriorityType) {
        this.trackerPriorityType = trackerPriorityType == null ? null : trackerPriorityType.getId();
    }

    public TrackerPriorityType getTrackerPriorityType() {
        return trackerPriorityType == null ? null : TrackerPriorityType.fromId(trackerPriorityType);
    }



    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setType(TicketsType type) {
        this.type = type == null ? null : type.getId();
    }

    public TicketsType getType() {
        return type == null ? null : TicketsType.fromId(type);
    }


    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public String getShortdesc() {
        return shortdesc;
    }


}