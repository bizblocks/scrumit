package com.company.scrumit.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Сущность для учета затраченного времени на задачу конкретным работником
 */
@Table(name = "SCRUMIT_HUMAN_RESOURCES_ACCOUNT")
@Entity(name = "scrumit_HumanResourcesAccount")
public class HumanResourcesAccount extends StandardEntity {
    private static final long serialVersionUID = -2750635808369849301L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_")
    protected Date date;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME")
    protected Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_T_IME")
    protected Date endTIme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMER_ID")
    protected Performer performer;

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public Performer getPerformer() {
        return performer;
    }

    public void setPerformer(Performer performer) {
        this.performer = performer;
    }

    public Date getEndTIme() {
        return endTIme;
    }

    public void setEndTIme(Date endTIme) {
        this.endTIme = endTIme;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}