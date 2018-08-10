package com.company.scrumit.web.screens.pokerplanning;

import com.company.scrumit.entity.*;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PokerPlanning extends AbstractWindow {

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    CollectionDatasource<Estimation, UUID> estimationsDs;
    @Inject
    UserSession userSession;
    @Inject
    DataManager dataManager;
    @Inject
    CollectionDatasource<Task, UUID> tasksDs;
    @Inject
    CollectionDatasource<Complexity, UUID> complexitiesLookupDs;
    @Inject
    private Metadata metadata;
    @Inject
    private Table<TreeTaskEstimation> taskEstimationsTable;

    @Inject
    private HierarchicalDatasource<TreeTaskEstimation, UUID> treeTaskEstimationsDs;

    private Complexity complexityDispute;
    private Collection<Task> tasks;
    private LinkedHashSet<UUID> existingTaskIdsInEstimations = new LinkedHashSet<>();
    private LinkedHashMap<UUID, TreeTaskEstimation> includedParentsByTaskId = new LinkedHashMap<>();
    private LinkedHashMap<UUID, TreeTaskEstimation> includedTasksByTaskId = new LinkedHashMap<>();
    private ArrayList<Complexity> toDeleteComplexities = new ArrayList<>();

    @Override
    public void init(Map<String, Object> params) {
        WindowParams.DISABLE_AUTO_REFRESH.set(params, true);

        complexityDispute = dataManager.load(Complexity.class)
                .id(UUID.fromString("00112233-4455-6677-8899-aabbccddeeff"))
                .view("_local")
                .one();
        complexitiesLookupDs.refresh();

        List<Performer> performersUserTeams = getPerformersUserTeams(getCurrentUserPerformer());
        tasksDs.refresh(Collections.singletonMap("performersUserTeams", performersUserTeams));

        tasks = tasksDs.getItems();
        initTreeTaskEstimationsDs();
        setupLookupPickerFieldForComplexity();

        getDsContext().addAfterCommitListener(
                (context, result) -> changeTaskAndParentTaskComplexities(result)
        );
    }

    private void initTreeTaskEstimationsDs() {
        List<Estimation> estimations = getEstimationsInDatabase();
        // LinkedHashSet<TreeTaskEstimation> includedParentTaskIds = new LinkedHashSet<>();

        for (Estimation e : estimations) {
            existingTaskIdsInEstimations.add(e.getTask().getId());
            includeToTreeTaskEstimations(e, e.getTask(), e.getTask().getTask());
            estimationsDs.includeItem(e);
        }
        for (Task t : tasks) {
            if (!existingTaskIdsInEstimations.contains(t.getId())) {
                Estimation estimation = metadata.create(Estimation.class);
                estimation.setTask(t);
                estimation.setUser(userSession.getUser());
                existingTaskIdsInEstimations.add(t.getId());
                includeToTreeTaskEstimations(estimation, t, t.getTask());
                estimationsDs.includeItem(estimation);
                // estimationsDs.includeItem(estimation);
            }
        }
        // remove duplicates if there are tasks with same performers as in parent tasks
        includedTasksByTaskId.forEach((uuid, treeTaskEstimation) -> {
            if (includedParentsByTaskId.containsKey(uuid)) {
                treeTaskEstimationsDs.excludeItem(treeTaskEstimation);
            }
        });
    }

    private void includeToTreeTaskEstimations(Estimation estimation, Task task, Task parentTask) {
        TreeTaskEstimation treeTaskEstimation = metadata.create(TreeTaskEstimation.class);
        treeTaskEstimation.setEstimation(estimation);
        treeTaskEstimation.setTask(task);

        if (parentTask != null) {
            TreeTaskEstimation parentTreeTaskEstimation;
            if (includedParentsByTaskId.containsKey(parentTask.getId())) {
                parentTreeTaskEstimation = includedParentsByTaskId.get(parentTask.getId());
            } else {
                parentTreeTaskEstimation = metadata.create(TreeTaskEstimation.class);
                parentTreeTaskEstimation.setTask(parentTask);
                includedParentsByTaskId.put(parentTask.getId(), parentTreeTaskEstimation);
            }

            treeTaskEstimation.setParent(parentTreeTaskEstimation);
            treeTaskEstimationsDs.includeItem(parentTreeTaskEstimation);
        }

        includedTasksByTaskId.put(task.getId(), treeTaskEstimation);
        treeTaskEstimationsDs.includeItem(treeTaskEstimation);
    }

    private Performer getCurrentUserPerformer() {
        LoadContext<Performer> loadContext = LoadContext.create(Performer.class)
                .setId(userSession.getUser().getId())
                .setView("performers-userTeams-view");

        return dataManager.load(loadContext);
    }

    private List<Performer> getPerformersUserTeams(Performer user) {
        if (user == null)
            return new ArrayList<>();
        Collection<Team> userTeams = user.getTeams();
        ArrayList<Performer> performersUserTeams = new ArrayList<>();
        for (Team userTeam : userTeams) {
            performersUserTeams.addAll(userTeam.getMembers());
        }

        return performersUserTeams;
    }

    private void setupLookupPickerFieldForComplexity() {
        taskEstimationsTable.addGeneratedColumn("estimation.complexity", treeTaskEstimation -> {
            LookupPickerField field = componentsFactory.createComponent(LookupPickerField.class);
            field.setWidth("100%");
            field.setOptionsDatasource(complexitiesLookupDs);
            field.setDatasource(taskEstimationsTable.getItemDatasource(treeTaskEstimation),
                    "estimation.complexity");
            field.addLookupAction();
            field.addClearAction();
            if (includedParentsByTaskId.containsKey(treeTaskEstimation.getTask().getId())) {
                return null;
            }

            return field;
        });
    }

    private void changeTaskAndParentTaskComplexities(Set<Entity> newEstimations) {
        HashSet<Task> parentTasksToCommit = new HashSet<>();
        HashSet<Task> tasksToCommit = new HashSet<>();
        for (Entity estimation :
                newEstimations) {
            Task task = ((Estimation) estimation).getTask();

            // TODO: there you can make loop there for deeper setting summary complexity
            Task parentTask = task.getTask();
            if (parentTask != null) {
                parentTasksToCommit.add(parentTask);
            }
            tasksToCommit.add(task);
        }

        // getDsContext().commit() not work because one CommitContext probably
        // ps already commit in other transaction

        commitTasksParentTasks(tasksToCommit, parentTasksToCommit);

        toDeleteComplexities.stream()
                .filter(complexity -> !complexity.equals(complexityDispute))
                .forEach(complexity -> dataManager.remove(complexity));

        toDeleteComplexities.clear();
    }

    private void commitTasksParentTasks(HashSet<Task> tasks, HashSet<Task> parentTasks) {
        Consumer<Task> commitConsumer = task -> dataManager.commit(task, "task-commit-complexity-view");
        tasks.forEach(this::prepareTask);
        tasks.forEach(commitConsumer);
        prepareParentTasks(parentTasks);
        parentTasks.forEach(commitConsumer);
    }

    private List<Estimation> getEstimationsInDatabase() {
        return dataManager.load(Estimation.class)
                .query("select e from scrumit$Estimation e where e.task.id in :tasks and e.user.id = :userId")
                .parameter("tasks", tasks)
                .parameter("userId", userSession.getUser().getId())
                .view("estimation-view")
                .list();
    }

    private Collection<Estimation> getAllEstimations() {
        return dataManager.load(Estimation.class)
                .query("select e from scrumit$Estimation e")
                .view("estimation-view")
                .list();
    }

    private boolean getShouldSetAverage(List<Estimation> taskEstimationsList, double average) {
        boolean shouldSetAverage = true;
        for (Estimation estimation :
                taskEstimationsList) {
            double complexityValue = estimation.getComplexity().getValue();
            if (complexityValue > 1.5 * average
                    || complexityValue < 0.5 * average) {
                shouldSetAverage = false;
                break;
            }
        }

        return shouldSetAverage;
    }

    private void setComplexityAverageOrDispute(Task task, boolean shouldSetAverage, double average) {
        String averageComplexityName = "Average complexity";
        if (!shouldSetAverage) {
            task.setComplexity(complexityDispute);

            return;
        }
        changeComplexity(task, average, averageComplexityName);
    }

    private void setComplexitySummary(Task parentTask, Collection<Task> childrenTasks) {
        String summaryComplexityName = "Summary complexity";
        Double summaryComplexityValue = childrenTasks
                .stream()
                .filter(task -> task.getComplexity() != null &&
                        !task.getComplexity().equals(complexityDispute))
                .map(task -> task.getComplexity().getValue())
                .reduce(0.0, (e1, e2) -> e1 + e2);

        changeComplexity(parentTask, summaryComplexityValue, summaryComplexityName);
    }

    private void changeComplexity(Task task, double value, String name) {
        Complexity complexity = task.getComplexity();
        if (complexity != null) {
            toDeleteComplexities.add(complexity);
        }
        complexity = metadata.create(Complexity.class);
        complexity.setValue(value);
        complexity.setName(name);
        complexity.setDuty(true);
        dataManager.commit(complexity);
        task.setComplexity(complexity);
    }

    private void prepareTask(Task task) {
        Collection<Estimation> allEstimations = getAllEstimations();
        List<Estimation> taskEstimations = allEstimations.stream()
                .filter(e -> e.getTask().getId().equals(task.getId()))
                .collect(Collectors.toList());
        double average = taskEstimations.stream()
                .mapToDouble(e -> e.getComplexity().getValue())
                .average()
                .orElse(0);

        setComplexityAverageOrDispute(
                task,
                getShouldSetAverage(taskEstimations, average),
                average
        );
    }

    private void prepareParentTasks(Collection<Task> parentTasksToCommit) {
        Collection<Task> parentTasks = dataManager.load(Task.class)
                .query("select t from scrumit$Task t where t.task.id in :parentTasks")
                .parameter("parentTasks", parentTasksToCommit)
                .view("task-parent-tasks-view")
                .list();

        for (Task parentTask :
                parentTasksToCommit) {
            Collection<Task> childrenTasks = parentTasks.stream()
                    .filter(task -> task.getTask().equals(parentTask))
                    .collect(Collectors.toList());
            setComplexitySummary(parentTask, childrenTasks);
        }
    }

    public void commit() {
        getDsContext().commit();
        // Necessary, else Object was modified in another transaction sometimes
        this.close("commit");
    }
}
