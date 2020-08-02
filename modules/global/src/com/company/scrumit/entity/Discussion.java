package com.company.scrumit.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.security.entity.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "SCRUMIT_DISCUSSION")
@Entity(name = "scrumit$Discussion")
public class Discussion extends StandardEntity {
    private static final long serialVersionUID = -8475236576406292745L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRACKER_ID")
    protected Tracker tracker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INITIATOR_ID")
    protected User initiator;

    @JoinTable(name = "SCRUMIT_DISCUSSION_USER_LINK",
            joinColumns = @JoinColumn(name = "DISCUSSION_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID"))
    @ManyToMany
    protected List<User> users;

    @Transient
    @MetaProperty
    protected List<Files> files;

    @OrderBy("createTs DESC")
    @OneToMany(mappedBy = "discussion", cascade = CascadeType.PERSIST)
    protected List<Message> messages = new ArrayList<>();

    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setFiles(List<Files> files) {
        this.files = files;
    }

    public List<Files> getFiles() {
        return files;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Tracker getTracker() {
        return tracker;
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }
}