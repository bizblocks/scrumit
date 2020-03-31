package com.company.scrumit.service;

import com.company.scrumit.config.ScrumitWebConfig;
import com.company.scrumit.entity.Performer;
import com.company.scrumit.entity.Task;
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
        LoadContext<Task> loadContext = LoadContext.create(Task.class)
                .setQuery(LoadContext.createQuery("select t from scrumit$Task t where t.stepName = :type1 or t.stepName is null")
                .setParameter("type1", "В работе"))
                .setView("Task-process");

        for(Task t:dataManager.loadList(loadContext)){
            if(commit.contains(t.getId().toString() + " done")){
                Performer performer = getPerformerByEmail(authorEmail);
                if(performer != null) {
                    t.setPerformer(performer);
                    dataManager.commit(t);
                    t = dataManager.reload(t, "Task-process");
                }
                try {
                    Stage stage = getStage(t);
                    if(stage == null) {
                        workflowService.startWorkflow(t, workflowService.determinateWorkflow(t));
                        t = dataManager.reload(t, "Task-process");
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

    private Stage getStage(Task task) {
        return dataManager.load(Stage.class)
                .query("select e from wfstp$Stage e where e.entityName = :entityName and e.name = :name")
                .parameter("entityName", task.getMetaClass().getName())
                .parameter("name", task.getStepName())
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
