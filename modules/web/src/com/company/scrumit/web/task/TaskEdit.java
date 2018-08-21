package com.company.scrumit.web.task;

import com.company.scrumit.entity.Status;
import com.company.scrumit.entity.Tracker;
import com.haulmont.cuba.core.app.DataService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.LoadContext.Query;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.web.gui.components.WebLookupPickerField;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.util.Map;

import static com.haulmont.cuba.gui.components.Frame.NotificationType.WARNING;

public class TaskEdit extends AbstractEditor<Task> {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }
}