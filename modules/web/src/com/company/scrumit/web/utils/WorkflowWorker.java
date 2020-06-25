package com.company.scrumit.web.utils;

import com.company.scrumit.entity.ExtWorkflowEntity;
import com.company.scrumit.entity.Task;
import com.company.scrumit.web.screens.CommentOnReturnScreen;
import com.company.scrumit.web.screens.Screen;
import com.google.common.base.Preconditions;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.Table;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;

@Component
public class WorkflowWorker {

    @Inject
    WorkflowService workflowService;
    @Inject
    private DataManager dataManager;

    public void returnTask(ExtWorkflowEntity entity, WorkflowInstanceTask task, Map<String, String> params, Frame screen, Messages messages){
        CommentOnReturnScreen returnScreen =(CommentOnReturnScreen) screen.openWindow("scrumit_CommentOnReturnScreen", WindowManager.OpenType.DIALOG);
        returnScreen.addCloseListener(actionId -> {
            if ("ok".equals(actionId)){
                String comment = returnScreen.getComment();
                    entity.setReturnComment(comment);
                    dataManager.commit(entity);
                    try {
                        workflowService.finishTask(task,params);
                    } catch (WorkflowException e) {
                        throw new RuntimeException("Ошибка обработки",e);
                    }finally {
                        Table taskTable = (Table) (screen.getComponent("taskTable"));
                        taskTable.getDatasource().refresh();
                    }
            }
        });


    }
}
