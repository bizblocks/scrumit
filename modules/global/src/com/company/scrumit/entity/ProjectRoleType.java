package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ProjectRoleType implements EnumClass<Integer> {

    Developer(10),
    QATester(20),
    Manager(30);

    private Integer id;

    ProjectRoleType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static ProjectRoleType fromId(Integer id) {
        for (ProjectRoleType at : ProjectRoleType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}