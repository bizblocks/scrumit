package com.company.scrumit.entity;

import com.groupstp.workflowstp.entity.WorkflowEntity;

public interface ExtWorkflowEntity<T> extends WorkflowEntity<T> {

    String getReturnComment();

    void setReturnComment(String comment);

}
