package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.Column;

@NamePattern("%s|party")
@Table(name = "SCRUMIT_CHAT_ROOM")
@Entity(name = "scrumit$ChatRoom")
public class ChatRoom extends StandardEntity {
    private static final long serialVersionUID = -5795726849547036165L;

    @JoinTable(name = "SCRUMIT_CHAT_ROOM_PERFORMER_LINK",
        joinColumns = @JoinColumn(name = "CHAT_ROOM_ID"),
        inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID"))
    @ManyToMany
    protected List<Performer> party;

    @Column(name = "ORIGIN", length = 150)
    protected String origin;

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrigin() {
        return origin;
    }


    public void setParty(List<Performer> party) {
        this.party = party;
    }

    public List<Performer> getParty() {
        return party;
    }


}