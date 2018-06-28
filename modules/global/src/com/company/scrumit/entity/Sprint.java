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

@NamePattern("%s - %s|periodStart,periodEnd")
@Table(name = "SCRUMIT_SPRINT")
@Entity(name = "scrumit$Sprint")
public class Sprint extends StandardEntity {
    private static final long serialVersionUID = -5801365616658805514L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_START")
    protected Date periodStart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMAND_ID")
    protected Team command;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_END")
    protected Date periodEnd;

    public Team getCommand() {
        return command;
    }

    public void setCommand(Team command) {
        this.command = command;
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