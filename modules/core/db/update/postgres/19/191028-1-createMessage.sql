create table SCRUMIT_MESSAGE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TEXT text,
    DISCUSSION_ID uuid,
    AUTOR_ID uuid,
    ATTACHMENT_ID uuid,
    --
    primary key (ID)
);