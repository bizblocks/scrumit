package com.company.scrumit.service;

import com.company.scrumit.entity.TaskClass;

public interface TaskClassService {
    String NAME = "scrumit_TaskClassService";

    /**
     * Обновляет среднее время выполнения {@link com.company.scrumit.entity.Task} для конкретного класса
     * @param taskClass конкретный класс
     * @return обновленное среднее время выполнения
     */
    Integer updateAverageHoursDurationForTaskClass(TaskClass taskClass);



}