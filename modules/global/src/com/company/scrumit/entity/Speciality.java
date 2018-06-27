package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NamePattern("%s|name")
@Table(name = "SCRUMIT_SPECIALITY")
@Entity(name = "scrumit$Speciality")
public class Speciality extends StandardEntity {
    private static final long serialVersionUID = -33753628721942124L;

    @Column(name = "NAME")
    protected String name;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Speciality getSpecialityByName(String name)
    {
        DataManager dataManager = AppBeans.get(DataManager.class);
        Speciality res = null;
        try {
             res = dataManager.load(Speciality.class)
                    .query("select c from scrumit$Speciality c where c.name=:name")
                    .parameter("name", name)
                    .view("_local")
                    .one();
        }
        catch (Exception e)
        {
            res  = new Speciality();
            res.setName(name);
            dataManager.commit(res);
        }
        return res;
    }

}