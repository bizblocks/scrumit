package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TrackerPriorityType implements EnumClass<String> {

    Critical("critical"),
    Cosmetic("cosmetic"),
    Hinder("hinder"),
    Inessential("inessential"),
    Speed("speed");

    private String id;

    TrackerPriorityType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TrackerPriorityType fromId(String id) {
        for (TrackerPriorityType at : TrackerPriorityType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}