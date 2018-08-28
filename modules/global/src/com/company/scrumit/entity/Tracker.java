package com.company.scrumit.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern("%s|shortdesc")
@Table(name = "SCRUMIT_TRACKER")
@Entity(name = "scrumit$Tracker")
public class Tracker extends StandardEntity {
    private static final long serialVersionUID = -8847125133735817612L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected Task project;

    @NotNull
    @Column(name = "SHORTDESC", nullable = false, length = 50)
    protected String shortdesc;

    @Column(name = "STATUS")
    protected String status;

    @Column(name = "TRACKER_PRIORITY_TYPE")
    protected String trackerPriorityType;

    @Column(name = "TYPE_")
    protected String type;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @OneToMany(mappedBy = "parentBug")
    protected List<Task> task;

    @OnDelete(DeletePolicy.CASCADE)
    @JoinTable(name = "SCRUMIT_TRACKER_FILE_DESCRIPTOR_LINK",
        joinColumns = @JoinColumn(name = "TRACKER_ID"),
        inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> files;

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




    public void setFiles(List<FileDescriptor> files) {
        this.files = files;
    }

    public List<FileDescriptor> getFiles() {
        return files;
    }

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