package com.company.scrumit.web.meeting;

import com.company.scrumit.entity.MeetingsTask;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
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

        Collection<KeyValueEntity> res = new HashSet<>();

        List<MeetingsTask> mtasks = dataManager.load(MeetingsTask.class)
                .query("select m from scrumit$MeetingsTask m " +
                        "where m.meeting.id=:meeting AND ((m.task.done=false) or (m.task.done IS NULL))")
                .parameter("meeting", params.get("meeting"))
                .view("meetingsTask-full")
                .list();
        mtasks.forEach(meetingsTask -> res.add(taskToKeyValue(meetingsTask.getTask(), meetingsTask)));

        List<Task> tasks = dataManager.load(Task.class)
                .query("select t from scrumit$Task t " +
                        "join t.sprints s " +
                        "where ((t.done IS NULL) or (t.done=false)) " +
                        "AND (s.id=:sprint)")
                .parameter("sprint", params.get("sprint"))
                .view("tasks-full")
                .list();
        tasks.forEach(task -> res.add(taskToKeyValue(task, null)));

        return res;
    }


    private KeyValueEntity taskToKeyValue(Task task, MeetingsTask mtask)
    {
        KeyValueEntity e = new KeyValueEntity();
        e.setValue("id", task.getUuid());
        e.setValue("parent", task.getTask());
        e.setValue("task", task);
        e.setValue("done", false);
        e.setValue("control", false);
        e.setValue("performer", task.getPerformer());
        e.setValue("begin", task.getBegin());
        e.setValue("deadline", task.getDeadline());
        e.setValue("comment", mtask==null ? "" : mtask.getComment());
        e.setValue("mtask", mtask);
        e.setIdName("id");
        return  e;
    }
}