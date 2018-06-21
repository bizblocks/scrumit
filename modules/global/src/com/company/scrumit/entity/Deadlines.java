package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s %s|task,deadline")
@Table(name = "SCRUMIT_DEADLINES")
@Entity(name = "scrumit$Deadlines")
public class Deadlines extends StandardEntity {
    private static final long serialVersionUID = 5640814162848147470L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DEADLINE")
    protected Date deadline;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }


    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getDeadline() {
        return deadline;
    }


}