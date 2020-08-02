package com.company.scrumit.service;

import com.company.scrumit.entity.Task;
import com.groupstp.mailreader.entity.ConnectionData;

public interface DaoService {
    String NAME = "scrumit_DaoService";

     ConnectionData findFirstConnectionDataByProject(Task task);
}