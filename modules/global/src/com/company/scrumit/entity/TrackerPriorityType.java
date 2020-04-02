package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;


public enum TrackerPriorityType implements EnumClass<Integer> {

    Critical(2),
    Cosmetic(6),
    Hinder(3),
    Inessential(7),
    Speed(4),
    Current(5),
    Security(1);

    private Integer id;

    TrackerPriorityType(int value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static TrackerPriorityType fromId(Integer id) {
        for (TrackerPriorityType at : TrackerPriorityType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}