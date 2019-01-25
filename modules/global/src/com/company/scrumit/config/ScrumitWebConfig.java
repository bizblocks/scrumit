package com.company.scrumit.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

@Source(type = SourceType.DATABASE)
public interface ScrumitWebConfig extends Config {

    @Property("payload.secret")
    @Default("secret")
    String getPayloadSecret();
    void setPayloadSecret(String value);
}
