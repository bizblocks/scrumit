create table SCRUMIT_SPRINT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERIOD_START timestamp,
    COMMAND_ID uuid,
    PERIOD_END timestamp,
    --
    primary key (ID)
);
