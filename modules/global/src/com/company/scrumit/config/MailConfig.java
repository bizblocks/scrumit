package com.company.scrumit.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.type.DateFactory;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.global.Secret;

import java.util.Date;

@Source(type = SourceType.DATABASE)
public interface MailConfig extends Config {

    @Property("mail.server")
    String getServer();
    void setServer(String value);

    @Property("mail.ssl")
    boolean getSsl();
    void setSsl(boolean value);

    @Property("mail.user")
    String getUser();
    void setUser(String value);

    @Property("mail.password")
    @Secret
    String getPassword();
    void setPassword(String value);

    //дата, начиная с которой идет синхронизация
    @Property("mail.last_message_date")
    @Factory(factory = DateFactory.class)
    @Default("2019-08-15 00:00:00.000")
    Date getLastMessageDate();
    void setLastMessageDate(String date);
}
