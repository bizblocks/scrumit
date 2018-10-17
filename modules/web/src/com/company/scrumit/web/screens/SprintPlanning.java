package com.company.scrumit.web.screens;

import com.company.scrumit.entity.Sprint;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.Team;
import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.cuba.web.gui.components.WebTreeTable;

import javax.inject.Inject;
import java.util.*;

public class SprintPlanning extends AbstractWindow {

    @Inject
    private HierarchicalDatasource<Task, UUID> unassignedTasksDs;
    @Inject
    private HierarchicalDatasource<Task, UUID> assignedTasksDs;
    @Inject
    private CollectionDatasourceImpl<Team, UUID> teamsDs;
    @Inject
    private Table<Task> unassignedTasksTable;
    @Inject
    private Table<Task> assignedTasksTable;
    @Inject
    private DataManager dataManager;
    @Inject
    private LookupPickerField team;
    @Inject
    private DateField dateStart;
    @Inject
    private Metadata metadata;

    private final int ONE_DAY = 24 * 60 * 60 * 1000;

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

        initListenersFroTables();

    }

    /**
     * initialize listeners for any components here!
     */
    private void initListenersFroTables() {

        Action selectUnassignedTasksTableAction = new BaseAction("") {
            @Override
            public void actionPerform(Component component) {
                Collection list = ((WebTreeTable) component).getSelected();
                if (list.size() == 0) {
                    return;
                }
                Task task = (Task) list.iterator().next();
                selectUnassignedTaskTableAction(task)
                ;
            }
        };
        unassignedTasksTable.setItemClickAction(selectUnassignedTasksTableAction);
        unassignedTasksTable.setEnterPressAction(selectUnassignedTasksTableAction);

        Action selectAssignedTasksTableAction = new BaseAction("") {
            @Override
            public void actionPerform(Component component) {
                Collection list = ((WebTreeTable) component).getSelected();
                if (list.size() == 0) {
                    return;
                }
                Task task = (Task) list.iterator().next();
                selectAssignedTasksTableAction(task)
                ;
            }
        };
        assignedTasksTable.setItemClickAction(selectAssignedTasksTableAction);
        assignedTasksTable.setEnterPressAction(selectAssignedTasksTableAction);

        teamsDs.addItemChangeListener(e -> refreshUnassignedTasksDs());
    }


    /**
     * manually refresh unassignedTasksDs after team has chosen
     */
    private void refreshUnassignedTasksDs() {
        if (teamsDs.getItem() == null) {
            clearDss();
            return;
        }

        List<Task> tasks = dataManager.loadList(LoadContext.create(Task.class).setQuery(LoadContext.createQuery(
                "select t from scrumit$Task t where (t.task in :team_projects) and (t.performer is NULL) and ((t.done = FALSE)or(t.done IS NULL))")
                .setParameter("team_projects", getTeamProjects())).setView("task-tree"));
        tasks.forEach(task -> unassignedTasksDs.addItem(task));
    }

    /**
     * @return Set with all projects (task) with currently access granted
     */
    private Set<Task> getTeamProjects() {
        Set<Task> projects = new HashSet<>();
        teamsDs.getItem().getProjects().forEach(t -> getTasksRecurr(projects, t));
        return projects;
    }

    /**
     *
     * @param result
     * @param task
     * @return
     */
    private Set<Task> getTasksRecurr(Set<Task> result, Task task) {
        result.add(task);
        dataManager.loadList(LoadContext.create(Task.class).setQuery(LoadContext.createQuery(
                "select t from scrumit$Task t where t.task.id=:parent").setParameter("parent", task)).setView("task-tree")).forEach(child -> getTasksRecurr(result, child));
        return result;
    }

    /**
     * Move selected task (with hierarchy) from unassigned task to assigned
     *
     * @param task selected task
     */
    private void selectAssignedTasksTableAction(Task task) {
        getTasksRecurr(new HashSet<>(), task).forEach(t -> {
            unassignedTasksDs.addItem(t);
            assignedTasksDs.removeItem(t);
        });
    }

    /**
     * Move selected task (with hierarchy) from assigned task to unassigned
     *
     * @param task selected task
     */
    private void selectUnassignedTaskTableAction(Task task) {
        getTasksRecurr(new HashSet<>(), task).forEach(t -> {
            assignedTasksDs.addItem(t);
            unassignedTasksDs.removeItem(t);
        });
    }

    /**
     * create sprint with selected date, assigned task
     */
    public void onBtnCreateSprintClick() {
        Collection<Task> tasks = assignedTasksDs.getItems();
        Sprint sprint = metadata.create(Sprint.class);
        sprint.setTeam(team.getValue());
        sprint.setPeriodStart(dateStart.getValue());
        Date date = dateStart.getValue();
        Integer sprintSize = ((Team) team.getValue()).getSprintSize();

        Preconditions.checkNotNullArgument(date, "Date start must bo not NULL");
        Preconditions.checkNotNullArgument(sprintSize, "Team's sprint size must be not NULL");

        date.setTime(date.getTime() + sprintSize * ONE_DAY);
        sprint.setPeriodEnd(date);
        sprint.setTasks(new HashSet<>(tasks));
        dataManager.commit(sprint);

        clearDss();

        showNotification(getMessage("sprintPlanning.created"), NotificationType.TRAY);
        UiEvent.push("sprintRefresh");
    }

    /**
     * clear data sources with tasks
     */
    private void clearDss() {
        unassignedTasksDs.clear();
        assignedTasksDs.clear();
        teamsDs.clear();
    }
}