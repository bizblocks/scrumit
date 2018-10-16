create table SCRUMIT_PROJECT_ROLE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TYPE_ integer not null,
    TEAM_ID uuid not null,
    PROJECT_ID uuid not null,
    PERFORMER_ID uuid not null,
    --
    primary key (ID)
);
