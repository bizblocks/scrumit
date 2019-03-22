package com.company.scrumit.service;

import com.company.scrumit.config.ScrumitWebConfig;
import com.company.scrumit.entity.Performer;
import com.company.scrumit.entity.Tracker;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service(GitService.NAME)
public class GitServiceBean implements GitService {

    @Inject
    private DataManager dataManager;

    @Inject
    private ScrumitWebConfig config;

    @Inject
    private WorkflowService workflowService;

    @Override
    public String getAuthenticationData(){
        return config.getPayloadSecret();
    }

    @Override
    public void updateTracker(String commit, String authorEmail) {
        LoadContext<Tracker> loadContext = LoadContext.create(Tracker.class)
                .setQuery(LoadContext.createQuery("select t from scrumit$Tracker t where t.stepName = :type1 or t.stepName = :type2 or t.stepName is null")
                .setParameter("type1", "Сделать")
                .setParameter("type2", "В работе"))
                .setView("_full");

        for(Tracker t:dataManager.loadList(loadContext)){
            if(commit.contains(t.getId().toString() + " done")){
                Performer performer = getPerformerByEmail(authorEmail);
                if(performer != null) {
                    t.setPerformer(performer);
                    dataManager.commit(t);
                    t = dataManager.reload(t, "_full");
                }
                try {
                    Stage stage = getStage(t);
                    if(stage == null) {
                        workflowService.startWorkflow(t, workflowService.determinateWorkflow(t));
                        t = dataManager.reload(t, "_full");
                        stage = getStage(t);
                    }
                    WorkflowInstanceTask instanceTask = workflowService.getWorkflowInstanceTaskNN(t, stage);
                        if (instanceTask != null) {
                            Map params = new HashMap();
                            params.put("toCheck", "true");
                            workflowService.finishTask(instanceTask, params);
                        }
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка обработки заявки", e);
                }
            }
        }
    }

    private Stage getStage(Tracker parentBug) {
        return dataManager.load(Stage.class)
                .query("select e from wfstp$Stage e where e.entityName = :entityName and e.name = :name")
                .parameter("entityName", parentBug.getMetaClass().getName())
                .parameter("name", parentBug.getStepName())
                .view("stage-process")
                .optional()
                .orElse(null);
    }

    private Performer getPerformerByEmail(String email) {
        LoadContext<Performer> loadContext = LoadContext.create(Performer.class)
                .setQuery(LoadContext.createQuery("select p from scrumit$Performer p where\n" +
                        "p.email=:email")
                        .setParameter("email", email))
                .setView("performer-view");

        return dataManager.load(loadContext);
    }

    @Override
    public String getTelegramBotName(){
       return config.getTelegramBotName();
    }

    @Override
    public String getTelegramBotToken(){
        return config.getTelegramBotToken();
    }

    @Override
    public String getTelegramChatId(String project){
        String chatId = dataManager.loadValue(
                "select p.telegramChatId from scrumit$ProjectTelegramChatIdLink p where " +
                        "p.projectName=:project", String.class)
                        .parameter("project", project).one();
        return chatId;
    }
}
