package com.company.scrumit.web.meeting;

import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.data.impl.CustomValueCollectionDatasource;
import com.haulmont.cuba.gui.data.impl.CustomValueHierarchicalDatasource;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class TasksForMeetingDs extends CustomValueHierarchicalDatasource {

    @Override
    protected Collection<KeyValueEntity> getEntities(Map<String, Object> params) {
        if(params.size()==0)
            params=getLastRefreshParameters();
        if(params.size()==0)
            return null;

        DataManager dataManager = AppBeans.get(DataManager.class);
        List<Task> tasks = dataManager.load(Task.class)
                .query("select t from scrumit$Task t " +
                        "join t.sprints s " +
                        "where ((t.done IS NULL) or (t.done=false)) " +
                        "AND (s.id=:sprint)")
                .parameter("sprint", params.get("sprint"))
                .view("tasks-full")
                .list();
        Collection<KeyValueEntity> res = new HashSet<>(tasksToKeyValue(tasks));

        tasks = dataManager.load(Task.class)
                .query("select m.task from scrumit$MeetingsTask m " +
                        "where m.meeting.id=:meeting AND ((m.task.done=false) or (m.task.done IS NULL))")
                .parameter("meeting", params.get("meeting"))
                .view("tasks-full")
                .list();
        res.addAll(tasksToKeyValue(tasks));
        return res;
    }

    private Collection<KeyValueEntity> tasksToKeyValue(List<Task> tasks)
    {
        Collection<KeyValueEntity> res = new HashSet<>();
        for (Task task : tasks) {
            KeyValueEntity e = new KeyValueEntity();
            e.setValue("parent", task.getTask());
            e.setValue("task", task);
            e.setValue("done", false);
            e.setValue("performer", task.getPerformer());
            e.setValue("begin", task.getBegin());
            e.setValue("deadline", task.getDeadline());
            res.add(e);
        }
        return res;
    }
}