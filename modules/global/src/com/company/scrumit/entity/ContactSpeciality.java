package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s %s|contact,speciality")
@Table(name = "SCRUMIT_CONTACT_SPECIALITY")
@Entity(name = "scrumit$ContactSpeciality")
public class ContactSpeciality extends StandardEntity {
    private static final long serialVersionUID = 6405107122996536684L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_ID")
    protected Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIALITY_ID")
    protected Speciality speciality;

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    public Speciality getSpeciality() {
        return speciality;
    }


}