package com.company.scrumit.web.taskclass;

import com.haulmont.cuba.gui.screen.*;
import com.company.scrumit.entity.TaskClass;

@UiController("scrumit_TaskClass.browse")
@UiDescriptor("task-class-browse.xml")
@LookupComponent("taskClassesTable")
@LoadDataBeforeShow
public class TaskClassBrowse extends StandardLookup<TaskClass> {
}