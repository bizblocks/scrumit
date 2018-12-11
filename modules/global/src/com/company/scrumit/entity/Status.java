package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum Status implements EnumClass<String> {
    ToDo("todo"),
    Doing("doing"),
    Done("done"),
    Testing("testing"),
    Checked("checked"),
    WontFix("wontfix"),
    NotABug("notabug");

    private String id;

    Status(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static Status fromId(String id) {
        for (Status at : Status.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}