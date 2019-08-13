package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "SCRUMIT_INCOMING_MESSAGE")
@Entity(name = "scrumit$IncomingMessage")
public class IncomingMessage extends StandardEntity {
    private static final long serialVersionUID = 5777441692630199376L;

    @Column(name = "SENDER", length = 150)
    protected String sender;

    @Column(name = "SUBJECT")
    protected String subject;

    @Lob
    @Column(name = "BODY_")
    protected String body;

    @Lob
    @Column(name = "RECIPIENTS")
    protected String recipients;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SEND_DATE")
    protected Date send_date;

    @Column(name = "ATTACHMENTS")
    protected byte[] attachments;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setSend_date(Date send_date) {
        this.send_date = send_date;
    }

    public Date getSend_date() {
        return send_date;
    }

    public void setAttachments(byte[] attachments) {
        this.attachments = attachments;
    }

    public byte[] getAttachments() {
        return attachments;
    }


}