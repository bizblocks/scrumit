package com.company.scrumit.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|task")
@MetaClass(name = "scrumit$TreeTaskEstimation")
public class TreeTaskEstimation extends BaseUuidEntity {
    private static final long serialVersionUID = -8591866120285018738L;

    @MetaProperty
    protected Task task;

    @MetaProperty
    protected Estimation estimation;

    @MetaProperty
    protected TreeTaskEstimation parent;

    public void setParent(TreeTaskEstimation parent) {
        this.parent = parent;
    }

    public TreeTaskEstimation getParent() {
        return parent;
    }


    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setEstimation(Estimation estimation) {
        this.estimation = estimation;
    }

    public Estimation getEstimation() {
        return estimation;
    }


}