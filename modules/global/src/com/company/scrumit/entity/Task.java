package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("scrumit_TaskListener")
@NamePattern("%s|shortdesc")
@Table(name = "SCRUMIT_TASK")
@Entity(name = "scrumit$Task")
public class Task extends StandardEntity {
    private static final long serialVersionUID = 8919522312858052940L;

    @NotNull
    @Column(name = "SHORTDESC", nullable = false, unique = true, length = 50)
    protected String shortdesc;

    @Column(name = "PROIRITY")
    protected String proirity;

    @Column(name = "REALDURATION")
    protected Integer realduration;

    @Column(name = "TYPE_")
    protected String type;

    @Column(name = "DESCRIPTION", length = 1024)
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMER_ID")
    protected Performer performer;

    @Temporal(TemporalType.DATE)
    @Column(name = "DEADLINE")
    protected Date deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @Column(name = "LEVEL_")
    protected Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP_ID")
    protected Task top;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BEGIN_")
    protected Date begin;

    @Transient
    @MetaProperty(related = {"begin", "deadline"})
    protected String duration;

    @Column(name = "AMOUNT")
    protected Integer amount;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "task")
    protected Tracker tracker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPRINT_BACKLOG_ID")
    protected SprintBacklog sprintBacklog;


    @JoinTable(name = "SCRUMIT_COMMAND_TASK_LINK",
        joinColumns = @JoinColumn(name = "TASK_ID"),
        inverseJoinColumns = @JoinColumn(name = "COMMAND_ID"))
    @ManyToMany
    protected List<Team> teams;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPRINT_ID")
    protected Sprint sprint;

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public Sprint getSprint() {
        return sprint;
    }


    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

    public void setTop(Task top) {
        this.top = top;
    }

    public Task getTop() {
        return top;
    }


    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return teams;
    }


    public void setProirity(Priority proirity) {
        this.proirity = proirity == null ? null : proirity.getId();
    }

    public Priority getProirity() {
        return proirity == null ? null : Priority.fromId(proirity);
    }


    public void setRealduration(Integer realduration) {
        this.realduration = realduration;
    }

    public Integer getRealduration() {
        return realduration;
    }



    public void setSprintBacklog(SprintBacklog sprintBacklog) {
        this.sprintBacklog = sprintBacklog;
    }

    public SprintBacklog getSprintBacklog() {
        return sprintBacklog;
    }


    public void setType(TaskType type) {
        this.type = type == null ? null : type.getId();
    }

    public TaskType getType() {
        return type == null ? null : TaskType.fromId(type);
    }


    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public Tracker getTracker() {
        return tracker;
    }


    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }


    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }


    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getBegin() {
        return begin;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPerformer(Performer performer) {
        this.performer = performer;
    }

    public Performer getPerformer() {
        return performer;
    }


    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getDeadline() {
        return deadline;
    }




    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public String getShortdesc() {
        return shortdesc;
    }


}