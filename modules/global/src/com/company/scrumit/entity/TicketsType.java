package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TicketsType implements EnumClass<String> {

    bug("bug"),
    feature("feature"),
    research("research");

    private String id;

    TicketsType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TicketsType fromId(String id) {
        for (TicketsType at : TicketsType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}