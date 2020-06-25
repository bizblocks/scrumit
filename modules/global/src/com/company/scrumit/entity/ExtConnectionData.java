package com.company.scrumit.entity;

import com.groupstp.mailreader.entity.ConnectionData;
import com.haulmont.cuba.core.entity.annotation.Extends;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity(name = "scrumit_ExtConnectionData")
@Extends(ConnectionData.class)
public class ExtConnectionData extends ConnectionData {
    private static final long serialVersionUID = -1084186593214631810L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected Task project;

    public Task getProject() {
        return project;
    }

    public void setProject(Task project) {
        this.project = project;
    }
}