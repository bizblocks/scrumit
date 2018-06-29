package com.company.scrumit.web.screens;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Team;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.LookupPickerField;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class Sprintplanning extends AbstractWindow {
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

    @Inject
    private DataManager dataManager;

    @Inject
    private LookupPickerField team;

    public void testQuery() {
        List<Task> tasks = dataManager.load(Task.class)
                .query("select e from scrumit$Task e where e.top in :projects")
                .parameter("projects", ((Team)team.getValue()).getProjects())
                .view("tasks-full")
                .list();
        System.out.println(tasks.toString());
    }
}