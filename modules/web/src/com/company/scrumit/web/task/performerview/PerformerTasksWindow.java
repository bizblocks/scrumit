package com.company.scrumit.web.task.performerview;

import com.company.scrumit.entity.Sprint;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskComment;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PerformerTasksWindow extends AbstractWindow {
    @Inject
    private TimeSource timeSource;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;

    @Inject
    private TreeTable<Task> tasksTable;
    @Inject
    private LookupField sprintsField;
    @Inject
    private CollectionDatasource<Sprint, UUID> sprintsDs;
    @Inject
    private HierarchicalDatasource<Task, UUID> tasksDs;
    @Inject
    private CollectionDatasource<TaskComment, UUID> commentsDs;
    @Inject
    private TextArea commentTextArea;
    @Inject
    private BoxLayout commentsBox;


    private User currentUser;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        currentUser = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();

        initSprintsField();
        initTasksTable();
        initCommentsTable();
    }

    private void initSprintsField() {
        sprintsDs.refresh(ParamsMap.of("userId", currentUser.getId()));

        Sprint currentSprint = null;
        Date now = timeSource.currentTimestamp();
        if (!CollectionUtils.isEmpty(sprintsDs.getItems())) {
            for (Sprint sprint : sprintsDs.getItems()) {
                if (isUnderPeriod(now, sprint.getPeriodStart(), sprint.getPeriodEnd())) {
                    currentSprint = sprint;
                    break;
                }
            }
        }
        sprintsField.setValue(currentSprint);
        sprintsField.addValueChangeListener(e -> refreshTasks());
    }

    private void initTasksTable() {
        tasksTable.addAction(new ItemTrackingAction(tasksTable, "done") {
            @Override
            public String getCaption() {
                return getMessage("performerTaskWindow.done.caption");
            }

            @Override
            public String getIcon() {
                return CubaIcon.EDITOR_OK.source();
            }

            @Override
            public void actionPerform(Component component) {
                final Task task = tasksTable.getSingleSelected();
                if (task != null) {
                    //ask before mark
                    BaseAction yes = new DialogAction(DialogAction.Type.YES, true).withHandler(e -> {
                        task.setDone(Boolean.TRUE);
                        dataManager.commit(task);

                        refreshTasks();
                    });

                    BaseAction cancel = new DialogAction(DialogAction.Type.CANCEL);

                    showOptionDialog(
                            messages.getMainMessage("dialogs.Confirmation"),
                            getMessage("performerTaskWindow.doneTaskConfirmation"),
                            MessageType.CONFIRMATION,
                            new Action[]{yes, cancel}
                    );
                }
            }

            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Task task = tasksTable.getSingleSelected();
                    return task != null && !Boolean.TRUE.equals(task.getDone());
                }
                return false;
            }
        });
        tasksTable.addAction(new RefreshAction(tasksTable, "refresh") {
            @Override
            public void actionPerform(Component component) {
                refreshTasks();

                commentsDs.refresh();//refresh comments too
            }
        });

        refreshTasks();
    }

    private void initCommentsTable() {
        commentsBox.setEnabled(false);
        tasksDs.addItemChangeListener(e -> {
            boolean taskSelected = e.getItem() != null;
            commentsBox.setEnabled(taskSelected);
            if (!taskSelected) {//cleanup not committed comment message
                commentTextArea.setValue(null);
            }
        });
    }

    private void refreshTasks() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getId());
        if (sprintsField.getValue() != null) {
            params.put("sprintId", ((Sprint) sprintsField.getValue()).getId());
        }
        tasksDs.refresh(params);
    }


    public void onComment() {
        String message = commentTextArea.getValue();
        Task task = tasksTable.getSingleSelected();
        if (!StringUtils.isEmpty(message) && task != null) {
            TaskComment comment = metadata.create(TaskComment.class);
            comment.setTask(task);
            comment.setMessage(message);
            dataManager.commit(comment);

            commentsDs.refresh();
            //cleanup entered text
            commentTextArea.setValue(null);
        }
    }

    private boolean isUnderPeriod(Date now, Date start, Date end) {
        return (start == null || start.compareTo(now) <= 0) && (end == null || end.compareTo(now) >= 0);
    }
}
