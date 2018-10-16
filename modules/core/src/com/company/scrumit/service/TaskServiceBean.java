package com.company.scrumit.service;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.company.scrumit.entity.Team;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Service(TaskService.NAME)
public class TaskServiceBean implements TaskService {

    @Inject
    private DataManager dataManager;

    @Override
    public Set<Task> tasksByProject(Task project) {
        return null;
    }

    @Override
    public Set<Task> tasksByTeam(Team team) {
        return null;
    }


    /**
     * check and set CONTROL flag for task that has not undone testing subtasks
     *
     * @param entity
     */
    @Override
    public void updateTesting(Task entity) {
        if (entity.getType() != TaskType.testing) return;

        Task parentTask = entity.getTask();

        if (parentTask.getControl() || !parentTask.getDone()) return;

        List<Task> tasks = dataManager.loadList(LoadContext.create(Task.class).setQuery(LoadContext.createQuery(
                "select t from scrumit$Task t where t.task.id=:parent")
                .setParameter("parent", parentTask)));

        if (tasks.stream().allMatch(Task::getDone)) {
            parentTask.setControl(true);
            dataManager.commit(parentTask);
        }
    }
}