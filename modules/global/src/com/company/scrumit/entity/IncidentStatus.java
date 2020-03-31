package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum IncidentStatus implements EnumClass<Integer> {

    NEW(1),
    IN_WORK(2),
    DONE(3),
    CANCELED(4);

    private Integer id;

    IncidentStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static IncidentStatus fromId(Integer id) {
        for (IncidentStatus at : IncidentStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}