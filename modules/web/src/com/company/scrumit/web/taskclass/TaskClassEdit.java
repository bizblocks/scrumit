package com.company.scrumit.web.taskclass;

import com.haulmont.cuba.gui.screen.*;
import com.company.scrumit.entity.TaskClass;

@UiController("scrumit_TaskClass.edit")
@UiDescriptor("task-class-edit.xml")
@EditedEntityContainer("taskClassDc")
@LoadDataBeforeShow
public class TaskClassEdit extends StandardEditor<TaskClass> {
}