create table SCRUMIT_PROJECT_TELEGRAM_CHAT_ID_LINK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROJECT_NAME varchar(50),
    TELEGRAM_CHAT_ID varchar(100),
    --
    primary key (ID)
);
