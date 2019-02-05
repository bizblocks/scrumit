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

    @Property("payload.username")
    @Default("admin")
    String getPayloadUsername();
    void setPayloadUsername(String value);

    @Property("payload.password")
    @Default("admin")
    String getPayloadPassword();
    void setPayloadPassword(String value);

    @Property("telegram.botName")
    @Default("botName")
    String getTelegramBotName();
    void setTelegramBotName(String value);

    @Property("telegram.botToken")
    @Default("botToken")
    String getTelegramBotToken();
    void setTelegramBotToken(String value);

    @Property("telegram.chatId")
    @Default("chatId")
    String getTelegramChatId();
    void setTelegramChatId(String value);
}
