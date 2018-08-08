package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.entity.FileDescriptor;

@NamePattern("%s|shortdesc")
@Table(name = "SCRUMIT_TRACKER")
@Entity(name = "scrumit$Tracker")
public class Tracker extends StandardEntity {
    private static final long serialVersionUID = -8847125133735817612L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected Task project;

    @OnDelete(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @NotNull
    @Column(name = "SHORTDESC", nullable = false, length = 50)
    protected String shortdesc;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @Column(name = "STATUS")
    protected String status;

    @Column(name = "TRACKER_PRIORITY_TYPE")
    protected String trackerPriorityType;

    @Column(name = "TYPE_")
    protected String type;

    @Column(name = "DESCRIPTION")
    protected String description;
    public void setStatus(Status status) {
        this.status = status == null ? null : status.getId();
    }

    public Status getStatus() {
        return status == null ? null : Status.fromId(status);
    }

    public void setTrackerPriorityType(TrackerPriorityType trackerPriorityType) {
        this.trackerPriorityType = trackerPriorityType == null ? null : trackerPriorityType.getId();
    }

    public TrackerPriorityType getTrackerPriorityType() {
        return trackerPriorityType == null ? null : TrackerPriorityType.fromId(trackerPriorityType);
    }


    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }






    public void setProject(Task project) {
        this.project = project;
    }

    public Task getProject() {
        return project;
    }


    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
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