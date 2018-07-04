package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.validation.constraints.NotNull;

@NamePattern("%s: %s|sprint,date")
@Table(name = "SCRUMIT_MEETING")
@Entity(name = "scrumit$Meeting")
public class Meeting extends StandardEntity {
    private static final long serialVersionUID = -1010168574694755600L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SPRINT_ID")
    protected Sprint sprint;

    @Column(name = "COMMENT_", length = 2048)
    protected String comment;

    @NotNull
    @Column(name = "TYPE_", nullable = false)
    protected String type;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }


    public MeetingType getType() {
        return type == null ? null : MeetingType.fromId(type);
    }

    public void setType(MeetingType type) {
        this.type = type == null ? null : type.getId();
    }



    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }


}