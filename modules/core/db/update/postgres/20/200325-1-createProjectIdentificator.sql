create table SCRUMIT_PROJECT_IDENTIFICATOR (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    IDENTIFICATOR varchar(255),
    PROJECT_ID uuid,
    --
    primary key (ID)
);