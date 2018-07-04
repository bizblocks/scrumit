create table SCRUMIT_SPRINT_TASK_LINK (
    SPRINT_ID uuid,
    TASK_ID uuid,
    primary key (SPRINT_ID, TASK_ID)
);
