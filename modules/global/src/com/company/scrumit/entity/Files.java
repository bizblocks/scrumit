package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.OneToOne;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.Column;
import javax.persistence.Lob;
import java.util.UUID;

@NamePattern("%s|description")
@Table(name = "SCRUMIT_FILES")
@Entity(name = "scrumit$Files")
public class Files extends StandardEntity {
    private static final long serialVersionUID = -8131675595929163812L;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "ENTITY")
    protected UUID entity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    public UUID getEntity() {
        return entity;
    }

    public void setEntity(UUID entity) {
        this.entity = entity;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }





}