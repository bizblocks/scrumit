package com.company.scrumit.service;

import com.company.scrumit.entity.ExtConnectionData;
import com.company.scrumit.entity.Task;
import com.groupstp.mailreader.entity.ConnectionData;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(DaoService.NAME)
public class DaoServiceBean implements DaoService {

    @Inject
    private DataManager dataManager;

    @Override
    public ConnectionData findFirstConnectionDataByProject(Task task) {
        return dataManager.load(ExtConnectionData.class)
                .query("select f from scrumit_ExtConnectionData f where f.project = :project")
                .parameter("project", task)
                .optional().orElse(null);
    }
}