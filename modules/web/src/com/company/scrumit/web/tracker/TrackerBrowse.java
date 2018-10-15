package com.company.scrumit.web.tracker;

import com.company.scrumit.entity.*;
import com.company.scrumit.web.entity.UiEvent;
import com.company.scrumit.web.task.TaskEdit;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class TrackerBrowse extends AbstractLookup {
    @Inject
    private CheckBox checkSelect;

    @Inject
    private GroupTable<Tracker> trackersTable;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Inject
    private UserSessionSource userSessionSource;

    /**
     * Called by the framework after creation of all components and before showing the screen.
     * <br> Override this method and put initialization logic here.
     *
     * @param params parameters passed from caller's code, usually from
     *               {@link #openWindow(String, WindowManager.OpenType)} and similar methods, or set in
     *               {@code screens.xml} for this registered screen
     */
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        checkSelect.addValueChangeListener(e -> trackersTable.setTextSelectionEnabled((Boolean) e.getValue()));
    }

    @EventListener
    public void onUiEvent(UiEvent event) {
        if ("trackerRefresh".equals(event.getSource())) {
            getDsContext().refresh();
        }
    }

    /**
     * take selected task (assign it to current user)
     */
    public void onTakeTaskBtnClick() {
        Tracker tracker = trackersTable.getSingleSelected();
        if (tracker == null) return;

        Performer currentPerfomer = dataManager.load(LoadContext.create(Performer.class).setQuery(LoadContext.createQuery(
                "select p from scrumit$Performer p where p.id=:id")
                .setParameter("id", userSessionSource.currentOrSubstitutedUserId())));

        if (currentPerfomer == null) return;

        tracker.setPerformer(currentPerfomer);
        dataManager.commit(tracker);

        if (((CheckBox) getComponent("createTask")).getValue()) {
            createTask(currentPerfomer);
        }
        getDsContext().refresh();
    }

    private void createTask(Performer currentPerfomer) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("perfomer", currentPerfomer);

        Task task = metadata.create(Task.class);

        TaskEdit editor = (TaskEdit) openEditor(task, WindowManager.OpenType.DIALOG, paramMap);
        ((LookupField) ((FieldGroup) editor.getComponent("fieldGroup")).getField("type").getComponent()).setValue(TaskType.task);
        ((LookupField) ((FieldGroup) editor.getComponent("fieldGroup")).getField("priority").getComponent()).setValue(Priority.Middle);
        editor.getDialogOptions().setResizable(true);
        editor.addCloseListener(actionId -> {
            UiEvent.push("taskRefresh");
        });
    }
}