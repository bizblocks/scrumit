package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.security.entity.User;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@NamePattern("%s|login")
@Entity(name = "scrumit$Performer")
public class Performer extends User {
    private static final long serialVersionUID = 6564112011977325025L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_ID")
    protected Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMAND_ID")
    protected Command command;

    @JoinTable(name = "SCRUMIT_CHAT_ROOM_PERFORMER_LINK",
        joinColumns = @JoinColumn(name = "PERFORMER_ID"),
        inverseJoinColumns = @JoinColumn(name = "CHAT_ROOM_ID"))
    @ManyToMany
    protected List<ChatRoom> chatRooms;

    public void setChatRooms(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }


    public void setCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }


    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }


}