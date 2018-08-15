package com.company.scrumit.web.task;

import com.company.scrumit.entity.Status;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.LoadContext.Query;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;

public class TaskEdit extends AbstractEditor<Task> {
    private static final long ONEDAY = 24*60*60*1000;

    @Named("fieldGroup.control")
    protected CheckBox control;

    @Inject
    private DataManager dataManager;

    @Named("fieldGroup.duration")
    private TextField durationField;

    @Named("fieldGroup.deadline")
    private DateField deadlineField;

    @Named("fieldGroup.begin")
    private DateField beginField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        durationField.addValueChangeListener(this::calcDates);
        beginField.addValueChangeListener(this::calcDates);
        deadlineField.addValueChangeListener(e -> {
            if(beginField.getValue()==null)
                return;
            durationField.setValue((deadlineField.getValue().getTime()-beginField.getValue().getTime())/ONEDAY);
        });
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (control.getValue()) {
            Query query = new Query("select t from scrumit$Tracker t where t.project.id = :id").setParameter("id", getItem().getId());
            LoadContext<Tracker> loadContext = LoadContext.create(Tracker.class)
                    .setQuery(query);
            Tracker tracker = dataManager.load(loadContext);
            if (tracker!= null) {
                tracker.setStatus(Status.Done);
                dataManager.commit(tracker);
            }
        }
        return super.postCommit(committed, close);
    }

    private void calcDates(ValueChangeEvent e) {
        if (beginField.getValue() == null || durationField.getValue() == null)
            return;
        Date d = beginField.getValue();
        d.setTime((d.getTime() + ONEDAY * (int)durationField.getValue()));
        deadlineField.setValue(d);
    }

}