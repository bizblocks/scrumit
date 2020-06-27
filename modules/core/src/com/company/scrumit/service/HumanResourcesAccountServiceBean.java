package com.company.scrumit.service;

import com.company.scrumit.utils.DateUtil;
import com.company.scrumit.utils.StringUtil;
import com.company.scrumit.entity.HumanResourcesAccount;
import com.company.scrumit.entity.Performer;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Date;

@Service(HumanResourcesAccountService.NAME)
public class HumanResourcesAccountServiceBean implements HumanResourcesAccountService {

    @Inject
    private DataManager dataManager;

    @Inject
    private StringUtil stringUtil;

    @Inject
    private DateUtil dateUtil;

    @Override
    public HumanResourcesAccount createRecord(User user, Task task, Date begin, Date end) {
        HumanResourcesAccount resourcesAccount = dataManager.create(HumanResourcesAccount.class);
        Duration betweenTwoDates = dateUtil.getDurationBetweenTwoDates(begin, end);
        resourcesAccount.setPerformer((Performer) user);
        resourcesAccount.setDate(new Date());
        resourcesAccount.setStartTime(begin);
        resourcesAccount.setEndTIme(end);
        resourcesAccount.setTask(task);
        resourcesAccount.setDuration(stringUtil.formatDurationToString(betweenTwoDates));
        return dataManager.commit(resourcesAccount);
    }
}