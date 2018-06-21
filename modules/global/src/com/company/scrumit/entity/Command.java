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

@NamePattern("%s|performer")
@Table(name = "SCRUMIT_COMMAND")
@Entity(name = "scrumit$Command")
public class Command extends StandardEntity {
    private static final long serialVersionUID = 1628222988045065473L;

    @OneToMany(mappedBy = "command")
    protected List<Performer> performer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEADER_ID")
    protected Performer leader;

    @JoinTable(name = "SCRUMIT_COMMAND_TASK_LINK",
        joinColumns = @JoinColumn(name = "COMMAND_ID"),
        inverseJoinColumns = @JoinColumn(name = "TASK_ID"))
    @ManyToMany
    protected List<Task> projects;

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


    public void setPerformer(List<Performer> performer) {
        this.performer = performer;
    }

    public List<Performer> getPerformer() {
        return performer;
    }


}