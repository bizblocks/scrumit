package com.company.scrumit.web.meeting;

import com.company.scrumit.entity.Meeting;
import com.company.scrumit.entity.MeetingType;
import com.company.scrumit.entity.MeetingsTask;
import com.company.scrumit.entity.Task;
import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MeetingEdit extends AbstractEditor<Meeting> {
    @Inject
    private CollectionDatasource<KeyValueEntity, Object> tasksForMeetingDs;

    @Named("fieldGroup.date")
    private DateField dateField;

    @Inject
    private TimeSource timeSource;

    @Named("fieldGroup.type")
    private LookupField typeField;

    @Named("fieldGroup.sprint")
    private PickerField sprintField;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Inject
    private TreeTable tasks;

    private Collection<Entity> changedEntities = new HashSet<>();

    /**
     * Hook to be implemented in subclasses. Called by the framework after committing datasources.
     * The default implementation notifies about commit and calls {@link #postInit()} if the window is not closing.
     *
     * @param committed whether any data were actually changed and committed
     * @param close     whether the window is going to be closed
     * @return true to continue, false to abort
     */
    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if(committed)
            changedEntities.forEach(e -> dataManager.commit(e));
        if (changedEntities.size()>0) UiEvent.push("taskRefresh");
        UiEvent.push("meetingRefresh");
        return super.postCommit(committed, close);
    }

    /**
     * Hook to be implemented in subclasses. Called by {@link #setItem(Entity)}.
     * At the moment of calling the main datasource is initialized and {@link #getItem()} returns reloaded entity instance.
     * <br>
     * This method can be called second time by {@link #postCommit(boolean, boolean)} if the window is not closed after
     * commit. Then {@link #getItem()} contains instance, returned from {@code DataService.commit()}.
     * This is useful for initialization of components that have to show fresh information from the current instance.
     * <br>
     * Example:
     * <pre>
     * protected void postInit() {
     *     if (!PersistenceHelper.isNew(getItem())) {
     *        diffFrame.loadVersions(getItem());
     *        entityLogDs.refresh();
     *    }
     * }
     * </pre>
     */
    @Override
    protected void postInit() {
        super.postInit();

        dateField.setValue(timeSource.currentTimestamp());
        typeField.setValue(MeetingType.SCRUM);

        Map<String, Object> dsparams = new HashMap<>();
        dsparams.put("meeting", this.getItem());
        dsparams.put("sprint", this.getItem().getSprint());
        tasksForMeetingDs.refresh(dsparams);

        tasks.expandAll();
        setListners(dsparams);
    }

    @Inject
    private Label comments;

    void setListners(Map<String, Object> dsparams)
    {
        sprintField.addValueChangeListener(e -> {
            dsparams.put("sprint", e.getValue());
            tasksForMeetingDs.refresh(dsparams);
            tasks.expandAll();
        });

        tasksForMeetingDs.addItemPropertyChangeListener(e -> {
            if("done".equals(e.getProperty()) || "control".equals(e.getProperty()))
            {
                Task t = (Task) e.getItem().getValue("task");
                if("done".equals(e.getProperty()))
                    t.setDone((Boolean) e.getValue());
                if("control".equals(e.getProperty()))
                    t.setControl((Boolean) e.getValue());
                changedEntities.add(t);
            }
            if("comment".equals(e.getProperty()))
            {
                MeetingsTask mtask = (MeetingsTask) e.getItem().getValue("mtask");
                if(mtask==null) {
                    mtask = metadata.create(MeetingsTask.class);
                    mtask.setTask(e.getItem().getValue("task"));
                    mtask.setMeeting(this.getItem());
                }
                mtask.setComment((String) e.getValue());
                changedEntities.add(mtask);
            }
        });

        tasksForMeetingDs.addItemChangeListener(e -> {
            StringBuilder s =  new StringBuilder();
            dataManager.load(MeetingsTask.class)
                    .query("select mt from scrumit$MeetingsTask mt where mt.task.id = :task")
                    .parameter("task", (Task)e.getItem().getValue("task"))
                    .list()
                    .forEach(i-> s.append(i.getComment()).append("\n"));
            comments.setValue(getMessage("Comments for task: ")+s);
        });
    }

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
    }
}