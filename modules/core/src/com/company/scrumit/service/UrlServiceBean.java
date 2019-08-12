package com.company.scrumit.service;

import com.haulmont.cuba.core.sys.AppContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service(UrlService.NAME)
public class UrlServiceBean implements UrlService {

    @Override
    @Transactional
    public String MakeOpenUrl(Command command, String screen, UUID entityId) {
        String nameDbEntity = screen.substring(0, screen.indexOf('.'));
        return new StringBuilder(AppContext.getProperty("cuba.webAppUrl"))
                //.append("/open?screen=")
                .append("/")
                .append(command.toString().toLowerCase())
                .append("?screen=")
                .append(screen)
                .append("&item=")
                .append(nameDbEntity)
                .append("-")
                .append(entityId)
                .toString();
    }
}