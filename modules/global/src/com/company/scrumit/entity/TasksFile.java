package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|file")
@Table(name = "SCRUMIT_TASKS_FILE")
@Entity(name = "scrumit$TasksFile")
public class TasksFile extends StandardEntity {
    private static final long serialVersionUID = -8131675595929163812L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected File file;

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }


}