package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|description")
@Entity(name = "scrumit$File")
public class File extends FileDescriptor {
    private static final long serialVersionUID = -9042557654654796945L;

    @Column(name = "DESCRIPTION")
    protected String description;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}