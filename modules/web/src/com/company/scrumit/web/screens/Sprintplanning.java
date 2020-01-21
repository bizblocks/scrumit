package com.company.scrumit.web.screens;

import com.company.scrumit.entity.Sprint;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Team;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.TwinColumn;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;

import javax.inject.Inject;
import java.util.*;

public class Sprintplanning extends AbstractWindow {
    @Inject
    private HierarchicalDatasource<Task, UUID> tasksDs;

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
        tasksDs.addCollectionChangeListener(e -> refreshUnassigned());
    }

    @Inject
    private DataManager dataManager;

    @Inject
    private LookupPickerField team;

    @Inject
    private TwinColumn twins;

    public void testQuery() {
        List<Task> tasks = dataManager.load(Task.class)
                .query("select e from scrumit$Task e where e.top in :projects")
                .parameter("projects", ((Team)team.getValue()).getProjects())
                .view("tasks-full")
                .list();
        System.out.println(tasks.toString());
    }

    private void refreshUnassigned()
    {
//        twins.
//        unassigned.removeAll();
//        Collection<Task> tasks = tasksDs.getItems();
//        for (Task t: tasks) {
//            GroupBoxLayout task = componentsFactory.createComponent(GroupBoxLayout.class);
//            task.setCaption(t.getInstanceName());
//            unassigned.add(task);
//        }
    }

    @Inject
    private DateField dateStart;

    private final int ONE_DAY = 24*60*60*1000;

    @Inject
    private Metadata metadata;

    public void onBtnCreateSprintClick() {
        Set<Task> s = (Set<Task>) twins.getValue();
        Sprint sprint = metadata.create(Sprint.class);
        sprint.setTeam((Team) team.getValue());
        sprint.setPeriodStart((Date) dateStart.getValue());
        Date d = (Date) dateStart.getValue();
        d.setTime(d.getTime()+((Team)team.getValue()).getSprintSize()*ONE_DAY);
        sprint.setPeriodEnd(d);
        sprint.setTasks(s);
        dataManager.commit(sprint);
    }
}