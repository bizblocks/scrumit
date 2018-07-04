create table SCRUMIT_MEETINGS_TASK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MEETING_ID uuid,
    TASK_ID uuid,
    COMMENT_ varchar(2048),
    --
    primary key (ID)
);
