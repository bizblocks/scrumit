package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;

@NamePattern("%s|name")
@Table(name = "SCRUMIT_CITY")
@Entity(name = "scrumit$City")
public class City extends StandardEntity {
    private static final long serialVersionUID = -4155940448946871926L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static City getCityByName(String name)
    {
        DataManager dataManager = AppBeans.get(DataManager.class);
        City res = null;
        try {
            res = dataManager.load(City.class)
                    .query("select c from scrumit$City c where c.name=:name")
                    .parameter("name", name)
                    .view("_local")
                    .one();

        }
        catch (Exception e)
        {
            res  = new City();
            res.setName(name);
            dataManager.commit(res);

        }
        return res;
    }
}