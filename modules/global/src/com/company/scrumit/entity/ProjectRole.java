package com.company.scrumit.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s|type")
@Table(name = "SCRUMIT_PROJECT_ROLE")
@Entity(name = "scrumit$ProjectRole")
public class ProjectRole extends StandardEntity {
    private static final long serialVersionUID = -117973607777205358L;

    @NotNull
    @Column(name = "TYPE_", nullable = false)
    protected Integer type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TEAM_ID")
    protected Team team;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROJECT_ID")
    protected Task project;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERFORMER_ID")
    protected Performer performer;

    public void setPerformer(Performer perfomer) {
        this.performer = perfomer;
    }

    public Performer getPerformer() {
        return performer;
    }


    public void setProject(Task project) {
        this.project = project;
    }

    public Task getProject() {
        return project;
    }


    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }


    public void setType(ProjectRoleType type) {
        this.type = type == null ? null : type.getId();
    }

    public ProjectRoleType getType() {
        return type == null ? null : ProjectRoleType.fromId(type);
    }


}