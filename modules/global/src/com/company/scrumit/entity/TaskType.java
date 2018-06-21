package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TaskType implements EnumClass<String> {

    task("task"),
    project("project"),
    prototype("prototype"),
    unplannedTask("unplannedTask");

    private String id;

    TaskType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TaskType fromId(String id) {
        for (TaskType at : TaskType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}