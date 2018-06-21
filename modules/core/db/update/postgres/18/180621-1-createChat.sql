create table SCRUMIT_CHAT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MESSAGE varchar(1024),
    SENDER_ID uuid,
    READ_ boolean,
    --
    primary key (ID)
);
