package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|id")
@Table(name = "SCRUMIT_CHAT")
@Entity(name = "scrumit$Chat")
public class Chat extends StandardEntity {
    private static final long serialVersionUID = -8141502178760719044L;

    @Column(name = "MESSAGE", length = 1024)
    protected String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ID")
    protected Performer sender;

    @Column(name = "READ_")
    protected Boolean read;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setSender(Performer sender) {
        this.sender = sender;
    }

    public Performer getSender() {
        return sender;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getRead() {
        return read;
    }


}