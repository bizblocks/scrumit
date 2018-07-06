package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|status")
@Table(name = "SCRUMIT_CONTACTS_STATUS")
@Entity(name = "scrumit$ContactsStatus")
public class ContactsStatus extends StandardEntity {
    private static final long serialVersionUID = 6353137410433002085L;

    @Column(name = "STATUS", length = 25)
    protected String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


}