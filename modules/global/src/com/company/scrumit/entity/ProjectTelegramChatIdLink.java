package com.company.scrumit.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|projectName")
@Table(name = "SCRUMIT_PROJECT_TELEGRAM_CHAT_ID_LINK")
@Entity(name = "scrumit$ProjectTelegramChatIdLink")
public class ProjectTelegramChatIdLink extends StandardEntity {
    private static final long serialVersionUID = -5903933563803386699L;

    @Column(name = "PROJECT_NAME", unique = true, length = 50)
    protected String projectName;

    @Column(name = "TELEGRAM_CHAT_ID", length = 100)
    protected String telegramChatId;

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setTelegramChatId(String telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public String getTelegramChatId() {
        return telegramChatId;
    }


}