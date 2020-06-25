package com.company.scrumit.service;

import com.company.scrumit.entity.HumanResourcesAccount;
import com.company.scrumit.entity.Performer;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

@Service(HumanResourcesAccountService.NAME)
public class HumanResourcesAccountServiceBean implements HumanResourcesAccountService {

    @Inject
    private DataManager dataManager;


    @Override
    public HumanResourcesAccount createRecord(User user, Task task, Date begin, Date end) {
        HumanResourcesAccount resourcesAccount = dataManager.create(HumanResourcesAccount.class);
        resourcesAccount.setPerformer((Performer) user);
        resourcesAccount.setDate(new Date());
        resourcesAccount.setStartTime(begin);
        resourcesAccount.setEndTIme(end);
        resourcesAccount.setTask(task);
        return dataManager.commit(resourcesAccount);
    }
}