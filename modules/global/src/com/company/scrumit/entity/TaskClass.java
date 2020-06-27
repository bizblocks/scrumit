package com.company.scrumit.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|localizedName")
@Table(name = "SCRUMIT_TASK_CLASS")
@Entity(name = "scrumit_TaskClass")
public class TaskClass extends StandardEntity {
    private static final long serialVersionUID = 3653236211117737621L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "LOCALIZED_NAME")
    protected String localizedName;

    @Column(name = "AVERAGE_DURATION_HOURS")
    protected Integer averageDurationHours;

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Integer getAverageDurationHours() {
        return averageDurationHours;
    }

    public void setAverageDurationHours(Integer averageDurationHours) {
        this.averageDurationHours = averageDurationHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}