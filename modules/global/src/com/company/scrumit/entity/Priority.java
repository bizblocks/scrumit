package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum Priority implements EnumClass<String> {

    Low("Low"),
    Middle("Middle"),
    High("High"),
    Urgent("Urgent");

    private String id;

    Priority(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static Priority fromId(String id) {
        for (Priority at : Priority.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}