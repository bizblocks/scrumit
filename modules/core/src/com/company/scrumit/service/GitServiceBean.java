package com.company.scrumit.service;

import com.company.scrumit.config.ScrumitWebConfig;
import com.company.scrumit.entity.Performer;
import com.company.scrumit.entity.Status;
import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;

@Service(GitService.NAME)
public class GitServiceBean implements GitService {

    @Inject
    private DataManager dataManager;

    @Inject
    private ScrumitWebConfig config;

    @Override
    public String getAuthenticationData(){
        return config.getPayloadSecret();
    }

    @Override
    public void updateTracker(String project, String commit, String authorEmail) {
        System.out.println("GitServiceBean.updateTracker");
        LoadContext<Tracker> loadContext = LoadContext.create(Tracker.class)
                .setQuery(LoadContext.createQuery("select t from scrumit$Tracker t where t.project.description = :project")
                        .setParameter("project", project))
                .setView("_full");

        for(Tracker t:dataManager.loadList(loadContext)){
            if(commit.contains(t.getId().toString())){
                if(t.getStatus()!=null && t.getStatus().equals(Status.Done))
                    continue;
                t.setStatus(Status.Done);
                Performer performer = getPerformerByEmail(authorEmail);
                if(performer != null)
                    t.setPerformer(performer);
                dataManager.commit(t);
            }
        }
    }

    private Performer getPerformerByEmail(String email) {
        LoadContext<Performer> loadContext = LoadContext.create(Performer.class)
                .setQuery(LoadContext.createQuery("select p from scrumit$Performer p where\n" +
                        "p.email=:email")
                        .setParameter("email", email))
                .setView("performer-view");

        return dataManager.load(loadContext);
    }
}
