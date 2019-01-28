package com.company.scrumit.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.security.entity.User;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s %s|login,name")
@Entity(name = "scrumit$Performer")
public class Performer extends User {
    private static final long serialVersionUID = 6564112011977325025L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_ID")
    protected Contact contact;

    @JoinTable(name = "SCRUMIT_CHAT_ROOM_PERFORMER_LINK",
        joinColumns = @JoinColumn(name = "PERFORMER_ID"),
        inverseJoinColumns = @JoinColumn(name = "CHAT_ROOM_ID"))
    @ManyToMany
    private List<ChatRoom> chatRooms;


    @JoinTable(name = "SCRUMIT_TEAM_PERFORMER_LINK",
        joinColumns = @JoinColumn(name = "PERFORMER_ID"),
        inverseJoinColumns = @JoinColumn(name = "TEAM_ID"))
    @ManyToMany
    protected List<Team> teams;


    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return teams;
    }


    public void setChatRooms(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }



    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }


}