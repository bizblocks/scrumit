package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s - %s: %s|meeting,task,comment")
@Table(name = "SCRUMIT_MEETINGS_TASK")
@Entity(name = "scrumit$MeetingsTask")
public class MeetingsTask extends StandardEntity {
    private static final long serialVersionUID = 7548228655065022692L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEETING_ID")
    protected Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @Column(name = "COMMENT_", length = 2048)
    protected String comment;

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


}