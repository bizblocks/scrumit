package com.company.scrumit.web.tracker;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

public enum TabType implements EnumClass<String> {
    NEW("new"), WORKFLOW(null);
    private final String id;

    TabType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
