package com.company.scrumit.web.task;

import com.company.scrumit.entity.Task;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Named;
import java.util.Date;
import java.util.Map;

public class TaskEdit extends AbstractEditor<Task> {
    private static final long ONEDAY = 24*60*60*1000;


    @Named("fieldGroup.deadline")
    private DateField deadlineField;
    @Named("fieldGroup.begin")
    private DateField beginField;
    @Named("fieldGroup.duration")
    private TextField durationField;

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

    private void calcDates(ValueChangeEvent e) {
        if (beginField.getValue() == null || durationField.getValue() == null)
            return;
        Date d = beginField.getValue();
        d.setTime((d.getTime() + ONEDAY * Double.valueOf(durationField.getValue()).longValue()));
        deadlineField.setValue(d);
    }

}