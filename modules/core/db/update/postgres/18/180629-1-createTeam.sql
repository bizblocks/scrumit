create table SCRUMIT_TEAM (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LEADER_ID uuid,
    SPRINT_SIZE integer,
    --
    primary key (ID)
);
