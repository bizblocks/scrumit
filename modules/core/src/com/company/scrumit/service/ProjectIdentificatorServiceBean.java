package com.company.scrumit.service;

import com.company.scrumit.entity.ProjectIdentificator;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Service(ProjectIdentificatorService.NAME)
public class ProjectIdentificatorServiceBean implements ProjectIdentificatorService {


    @Inject
    private DataManager dataManager;

    /**
     *
     * @param tracker
     * @return null, если не найден идентификатор проекта или у последнего созданного инцидента нет номера
     */
    @Override
    @Nullable
    public String generateTrackerNumber(Tracker tracker) {

        ProjectIdentificator identificator = dataManager.load(ProjectIdentificator.class)
                .query("select f from scrumit$ProjectIdentificator f where f.project = :project")
                .parameter("project",tracker.getProject())
                .view("projectIdentificator-view")
                .optional().orElse(null);

        if (identificator==null){
            return null;
        }
        Tracker lastTracker = dataManager.load(Tracker.class)
                .query("select f from scrumit$Tracker f order by f.createTs desc")
                .view("tracker-number")
                .one();
        long number;
        if (lastTracker.getNumber()!=null){
            number = Long.parseLong(lastTracker.getNumber().replaceAll(identificator.getIdentificator(),""));

        }else {
            return null;
        }
        return identificator.getIdentificator()+(number+1);
    }
    @Override
    public ProjectIdentificator getProjectIdentificatorByProject(Task project){
        return dataManager.load(ProjectIdentificator.class)
                .query("select f from scrumit$ProjectIdentificator f where f.project= :project")
                .parameter("project",project)
                .view("projectIdentificator-view")
                .optional().orElse(null);
    }
}