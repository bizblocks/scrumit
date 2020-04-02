package com.company.scrumit.service;

import com.company.scrumit.entity.HumanResourcesAccount;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.security.entity.User;

import java.util.Date;

public interface HumanResourcesAccountService {
    String NAME = "scrumit_HumanResourcesAccountService";

    HumanResourcesAccount createRecord(User user, Task task, Date begin, Date end);
}