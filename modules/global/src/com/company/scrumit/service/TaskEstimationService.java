package com.company.scrumit.service;

import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskEstimation;

import java.util.List;

public interface TaskEstimationService {
    String NAME = "scrumit_TaskEstimationService";

    void estimateTask(TaskEstimation estimation, Task task);

    List<TaskEstimation> findAllSorted();
}