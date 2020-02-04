package com.company.scrumit.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@NamePattern("%s|taskId")
@Table(name = "SCRUMIT_TASK_ESTIMATION")
@Entity(name = "scrumit$TaskEstimation")
public class TaskEstimation extends StandardEntity {
    private static final long serialVersionUID = 9092560208258651043L;

    @NotNull
    @Column(name = "taskId")
    protected UUID taskId;

    @NotNull
    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "USERID")
    protected UUID userID;

    @NotNull
    @Column(name = "VALUE_")
    protected Double value;

    public void setUserID(UUID userID) {this.userID = userID;}

    public UUID getUserID() {return userID;}

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
}