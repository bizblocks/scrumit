create table SCRUMIT_INCOMING_MESSAGE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SENDER varchar(150),
    SUBJECT varchar(255),
    BODY_ text,
    RECIPIENTS text,
    SEND_DATE timestamp,
    ATTACHMENTS bytea,
    --
    primary key (ID)
);
