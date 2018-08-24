package com.company.scrumit.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

@Listeners({"scrumit_TaskListener", "scrumit_TaskEntityListener"})
@NamePattern("%s|shortdesc")
@Table(name = "SCRUMIT_TASK")
@Entity(name = "scrumit$Task")
public class Task extends StandardEntity {
    private static final long serialVersionUID = 8919522312858052940L;

    @NotNull
    @Column(name = "SHORTDESC", nullable = false, unique = true, length = 50)
    protected String shortdesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_BUG_ID")
    protected Tracker parentBug;

    @OneToMany(mappedBy = "project")
    @OrderBy("createTs")
    protected List<Tracker> tracker;

    @Column(name = "DONE")
    protected Boolean done;

    @Column(name = "CONTROL")
    protected Boolean control;

    @Column(name = "PROIRITY")
    protected String proirity;

    @Column(name = "REALDURATION")
    protected Integer realduration;

    @Column(name = "TYPE_")
    protected String type;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "clear"})
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

    @Column(name = "DURATION")
    protected String duration;

    @Column(name = "AMOUNT")
    protected Integer amount;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPRINT_BACKLOG_ID")
    protected SprintBacklog sprintBacklog;


    @JoinTable(name = "SCRUMIT_COMMAND_TASK_LINK",
        joinColumns = @JoinColumn(name = "TASK_ID"),
        inverseJoinColumns = @JoinColumn(name = "COMMAND_ID"))
    @ManyToMany
    protected List<Team> teams;

    @JoinTable(name = "SCRUMIT_SPRINT_TASK_LINK",
        joinColumns = @JoinColumn(name = "TASK_ID"),
        inverseJoinColumns = @JoinColumn(name = "SPRINT_ID"))
    @ManyToMany
    protected List<Sprint> sprints;

    @Column(name = "SS_ID")
    protected Long ssId;

    public void setParentBug(Tracker parentBug) {
        this.parentBug = parentBug;
    }

    public Tracker getParentBug() {
        return parentBug;
    }


    public void setTracker(List<Tracker> tracker) {
        this.tracker = tracker;
    }

    public List<Tracker> getTracker() {
        return tracker;
    }

    public void setSsId(Long ssId) {
        this.ssId = ssId;
    }

    public Long getSsId() {
        return ssId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setControl(Boolean control) {
        this.control = control;
    }

    public Boolean getControl() {
        return control;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Boolean getDone() {
        return done;
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