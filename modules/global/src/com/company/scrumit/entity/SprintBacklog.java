package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s %s|sprint,tasks")
@Table(name = "SCRUMIT_SPRINT_BACKLOG")
@Entity(name = "scrumit$SprintBacklog")
public class SprintBacklog extends StandardEntity {
    private static final long serialVersionUID = 4570670954990208147L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPRINT_ID")
    protected Sprint sprint;

    @OneToMany(mappedBy = "sprintBacklog")
    protected List<Task> tasks;

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }


}