package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NamePattern("%s|description")
@Entity(name = "scrumit$File")
public class File extends FileDescriptor {
    private static final long serialVersionUID = -9042557654654796945L;

    @Column(name = "DESCRIPTION")
    protected String description;




    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor fileDescriptor;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRACKER_ID")
    protected Tracker tracker;



    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public Tracker getTracker() {
        return tracker;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public FileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }

    public void setFileDescriptor(FileDescriptor fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
    }

}