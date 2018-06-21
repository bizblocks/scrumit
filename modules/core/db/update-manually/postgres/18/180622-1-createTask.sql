create table SCRUMIT_TASK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SHORTDESC varchar(50) not null,
    DESCRIPTION varchar(1024),
    PERFORMER_ID uuid,
    DEADLINE date,
    TASK_ID uuid,
    BEGIN_ timestamp,
    AMOUNT integer,
    --
    primary key (ID)
);
