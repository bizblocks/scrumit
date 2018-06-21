package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum MeetingType implements EnumClass<String> {

    SCRUM("SCRUM"),
    CrossSCRUM("CROSSSCRUM"),
    Retro("RETRO"),
    SCRUM_ON_SCRUM("SCRUMONSCRUM");

    private String id;

    MeetingType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static MeetingType fromId(String id) {
        for (MeetingType at : MeetingType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}