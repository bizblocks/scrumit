package com.company.scrumit.service;

import com.company.scrumit.core.StringUtil;
import com.company.scrumit.entity.IncidentStatus;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(TrackerService.NAME)
public class TrackerServiceBean implements TrackerService {

    @Inject
    private DataManager dataManager;
    @Inject
    private StringUtil stringUtil;

    @Override
    public IncidentStatus updateIncidentStatus(Tracker incident) {
        incident = dataManager.reload(incident,"tracker-newTab");
        IncidentStatus result = null;
        Boolean isNew = Boolean.FALSE;
        Boolean isDone = Boolean.FALSE;
        Boolean isInWork = Boolean.FALSE;
        if (!incident.getTask().isEmpty()) {
            for (Task task : incident.getTask()) {
                task = dataManager.reload(task, "task-tree");
                result = checkStatusByChildrenRecursive(isNew, isDone, isInWork, task);
                incident.setIncidentStatus(result);
            }
        }else {
            result = IncidentStatus.NEW;
            incident.setIncidentStatus(result);
        }
        dataManager.commit(incident);
        return result;
    }

    public IncidentStatus checkStatusByChildrenRecursive(Boolean isNew, Boolean isDone, Boolean isInWork, Task parent) {
        if (!parent.getChildren().isEmpty()) {
            for (Task task : parent.getChildren()) {
                task = dataManager.reload(task, "task-tree");
                return checkStatusByChildrenRecursive(isNew, isDone, isInWork, task);
            }
        }
        if (parent.getStatus() == null) {
            return IncidentStatus.NEW;
        } else {
            switch (parent.getStatus()) {
                case DONE:
                    isDone = Boolean.TRUE;
                    break;
                case IN_PROGRESS:
                    isInWork =  Boolean.TRUE;
                    break;
                case FAILED:
                    isDone =  Boolean.TRUE;
                    break;
                default:
                    isNew =  Boolean.TRUE;
            }
            if ((isDone && isNew) || isInWork) {
               return IncidentStatus.IN_WORK;
            } else if (isNew) {
                return IncidentStatus.NEW;
            } else return IncidentStatus.DONE;
        }

    }

    @Override
    public String getEmailFormString(String source){
        return stringUtil.getEmailFromString(source);
    }
}