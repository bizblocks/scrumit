create table SCRUMIT_HUMAN_RESOURCES_ACCOUNT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TASK_ID uuid,
    DATE_ timestamp,
    START_TIME timestamp,
    END_T_IME timestamp,
    PERFORMER_ID uuid,
    --
    primary key (ID)
);