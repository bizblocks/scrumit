package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Column;

@NamePattern("%s|name")
@Table(name = "SCRUMIT_TEAM")
@Entity(name = "scrumit$Team")
public class Team extends StandardEntity {
    private static final long serialVersionUID = 1628222988045065473L;

    @Column(name = "NAME", length = 20)
    protected String name;

    @JoinTable(name = "SCRUMIT_TEAM_PERFORMER_LINK",
        joinColumns = @JoinColumn(name = "TEAM_ID"),
        inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID"))
    @ManyToMany
    protected List<Performer> members;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEADER_ID")
    protected Performer leader;

    @JoinTable(name = "SCRUMIT_COMMAND_TASK_LINK",
        joinColumns = @JoinColumn(name = "COMMAND_ID"),
        inverseJoinColumns = @JoinColumn(name = "TASK_ID"))
    @ManyToMany
    protected List<Task> projects;

    @Column(name = "SPRINT_SIZE")
    protected Integer sprintSize;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setMembers(List<Performer> members) {
        this.members = members;
    }

    public List<Performer> getMembers() {
        return members;
    }


    public void setSprintSize(Integer sprintSize) {
        this.sprintSize = sprintSize;
    }

    public Integer getSprintSize() {
        return sprintSize;
    }


    public void setProjects(List<Task> projects) {
        this.projects = projects;
    }

    public List<Task> getProjects() {
        return projects;
    }


    public void setLeader(Performer leader) {
        this.leader = leader;
    }

    public Performer getLeader() {
        return leader;
    }



}