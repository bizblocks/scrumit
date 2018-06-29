package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.Set;
import javax.persistence.OneToMany;

@NamePattern("%s - %s|periodStart,periodEnd")
@Table(name = "SCRUMIT_SPRINT")
@Entity(name = "scrumit$Sprint")
public class Sprint extends StandardEntity {
    private static final long serialVersionUID = -5801365616658805514L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_START")
    protected Date periodStart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    protected Team team;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_END")
    protected Date periodEnd;



    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "sprint")
    protected Set<Task> tasks;

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Task> getTasks() {
        return tasks;
    }


    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }


    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }


}