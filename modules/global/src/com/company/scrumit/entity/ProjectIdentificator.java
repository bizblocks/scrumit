package com.company.scrumit.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Сущность для хранения идентификаторов проектов для создания номера инцидента
 */
@Table(name = "SCRUMIT_PROJECT_IDENTIFICATOR")
@Entity(name = "scrumit$ProjectIdentificator")
public class ProjectIdentificator extends StandardEntity {
    private static final long serialVersionUID = 9037477772756905379L;

    @NotNull
    @Column(name = "IDENTIFICATOR", unique = true)
    protected String identificator;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    protected Task project;

    public Task getProject() {
        return project;
    }

    public void setProject(Task project) {
        this.project = project;
    }

    public String getIdentificator() {
        return identificator;
    }

    public void setIdentificator(String identificator) {
        this.identificator = identificator;
    }
}