package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.Date;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Versioned;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.Creatable;

@NamePattern("%s - %s|value,name")
@Table(name = "SCRUMIT_COMPLEXITY")
@Entity(name = "scrumit$Complexity")
public class Complexity extends BaseUuidEntity implements Versioned, Updatable, Creatable {
    private static final long serialVersionUID = 3292176796541762212L;

    @Column(name = "NAME", length = 50)
    protected String name;


    @Column(name = "VALUE_")
    protected Double value;

    @Column(name = "DUTY", nullable = false)
    protected Boolean duty = false;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    @Version
    @Column(name = "VERSION", nullable = false)
    protected Integer version;

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Override
    public Date getCreateTs() {
        return createTs;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public Integer getVersion() {
        return version;
    }


    public void setDuty(Boolean duty) {
        this.duty = duty;
    }

    public Boolean getDuty() {
        return duty;
    }


    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}