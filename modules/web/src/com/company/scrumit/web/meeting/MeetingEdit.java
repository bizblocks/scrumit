package com.company.scrumit.web.meeting;

import com.company.scrumit.entity.MeetingType;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.scrumit.entity.Meeting;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
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

        sprintField.addValueChangeListener(e -> {
            dsparams.put("sprint", e.getValue());
            tasksForMeetingDs.refresh(dsparams);
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