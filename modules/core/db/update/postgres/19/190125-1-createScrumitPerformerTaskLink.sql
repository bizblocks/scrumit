create table SCRUMIT_PERFORMER_TASK_LINK (
    TASK_ID uuid,
    PERFORMER_ID uuid,
    primary key (TASK_ID, PERFORMER_ID)
);
