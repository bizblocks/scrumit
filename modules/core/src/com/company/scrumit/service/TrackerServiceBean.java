package com.company.scrumit.service;

import com.haulmont.cuba.core.app.ServerConfig;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.sys.AppContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@Service(TrackerService.NAME)
public class TrackerServiceBean implements TrackerService {

    @Inject
    private ServerConfig serverConfig;

    @Override
    @Transactional
    public String MakeOpenUrl(String screen, UUID entityId) {
        String dbName = screen.substring(0, screen.indexOf('$'));
        return new StringBuilder(AppContext.getProperty("cuba.webAppUrl"))
                .append("/open?screen=")
                .append(screen)
                .append("&item=")
                .append(dbName)
                .append("$")
                .append(String.valueOf(entityId))
                .toString();
    }
}