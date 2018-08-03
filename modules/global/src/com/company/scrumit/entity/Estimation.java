package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.DesignSupport;
import com.haulmont.cuba.security.entity.User;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s - %s|task,complexity")
@Table(name = "SCRUMIT_ESTIMATION", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_SCRUMIT_ESTIMATION_UNQ", columnNames = {"USER_ID", "TASK_ID"})
})
@Entity(name = "scrumit$Estimation")
public class Estimation extends StandardEntity {
    private static final long serialVersionUID = 5763718442628157978L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    protected User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPLEXITY_ID")
    protected Complexity complexity;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setComplexity(Complexity complexity) {
        this.complexity = complexity;
    }

    public Complexity getComplexity() {
        return complexity;
    }


}