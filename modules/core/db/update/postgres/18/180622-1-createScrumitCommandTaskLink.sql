create table SCRUMIT_COMMAND_TASK_LINK (
    TASK_ID uuid,
    COMMAND_ID uuid,
    primary key (TASK_ID, COMMAND_ID)
);
