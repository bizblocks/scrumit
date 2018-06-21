create table SCRUMIT_MEETING (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SPRINT_ID uuid,
    TYPE_ varchar(50),
    DATE_ date,
    --
    primary key (ID)
);
