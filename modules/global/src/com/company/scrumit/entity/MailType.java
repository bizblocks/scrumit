package com.company.scrumit.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum MailType implements EnumClass<String> {

    Request("request"),
    Spam("spam");

    private String id;

    MailType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static MailType fromId(String id) {
        for (MailType at : MailType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}