-- begin SCRUMIT_SPECIALITY
create table SCRUMIT_SPECIALITY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    --
    primary key (ID)
)^
-- end SCRUMIT_SPECIALITY
-- begin SCRUMIT_CONTACT
create table SCRUMIT_CONTACT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FIO varchar(255) not null,
    CONTACTS varchar(255),
    STATUS_ID uuid,
    CITY_ID uuid,
    EMAIL varchar(255) not null,
    PHONE varchar(255),
    COMMENTS varchar(1024),
    --
    primary key (ID)
)^
-- end SCRUMIT_CONTACT
-- begin SCRUMIT_CITY
create table SCRUMIT_CITY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end SCRUMIT_CITY
-- begin SCRUMIT_CONTACTS_SPECIALITY
create table SCRUMIT_CONTACTS_SPECIALITY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CONTACT_ID uuid,
    SPECIALITY_ID uuid,
    --
    primary key (ID)
)^
-- end SCRUMIT_CONTACTS_SPECIALITY
-- begin SCRUMIT_TEAM
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
    NAME varchar(20),
    LEADER_ID uuid,
    SPRINT_SIZE integer,
    SMARTSHEET_ID bigint,
    --
    primary key (ID)
)^
-- end SCRUMIT_TEAM
-- begin SCRUMIT_TASK
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
    DONE boolean,
    CONTROL boolean,
    PROIRITY varchar(50),
    REALDURATION integer,
    TYPE_ varchar(50),
    DESCRIPTION text,
    PERFORMER_ID uuid,
    DEADLINE date,
    TASK_ID uuid,
    LEVEL_ integer,
    TOP_ID uuid,
    BEGIN_ timestamp,
    DURATION varchar(255),
    AMOUNT integer,
    SPRINT_BACKLOG_ID uuid,
    SS_ID bigint,
    --
    primary key (ID)
)^
-- end SCRUMIT_TASK
-- begin SCRUMIT_TASK_COMMENT
create table SCRUMIT_TASK_COMMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TASK_ID uuid not null,
    MESSAGE text not null,
    --
    primary key (ID)
)^
-- end SCRUMIT_TASK_COMMENT
-- begin SCRUMIT_SPRINT
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
    TEAM_ID uuid,
    PERIOD_END timestamp,
    --
    primary key (ID)
)^
-- end SCRUMIT_SPRINT
-- begin SCRUMIT_MEETING
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
    SPRINT_ID uuid not null,
    COMMENT_ varchar(2048),
    TYPE_ varchar(50) not null,
    DATE_ date not null,
    --
    primary key (ID)
)^
-- end SCRUMIT_MEETING
-- begin SCRUMIT_TRACKER
create table SCRUMIT_TRACKER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROJECT_ID uuid,
    FILE_ID uuid,
    SHORTDESC varchar(50) not null,
    TASK_ID uuid,
    STATUS varchar(50),
    TRACKER_PRIORITY_TYPE varchar(50),
    TYPE_ varchar(50),
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end SCRUMIT_TRACKER
-- begin SCRUMIT_CHAT
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
)^
-- end SCRUMIT_CHAT
-- begin SCRUMIT_CHAT_ROOM
create table SCRUMIT_CHAT_ROOM (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORIGIN varchar(150),
    --
    primary key (ID)
)^
-- end SCRUMIT_CHAT_ROOM
-- begin SCRUMIT_DEADLINES
create table SCRUMIT_DEADLINES (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TASK_ID uuid,
    DEADLINE timestamp,
    --
    primary key (ID)
)^
-- end SCRUMIT_DEADLINES
-- begin SCRUMIT_SPRINT_BACKLOG
create table SCRUMIT_SPRINT_BACKLOG (
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
    --
    primary key (ID)
)^
-- end SCRUMIT_SPRINT_BACKLOG
-- begin SCRUMIT_LINK
create table SCRUMIT_LINK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TASK_ID uuid,
    URL varchar(255),
    --
    primary key (ID)
)^
-- end SCRUMIT_LINK
-- begin SCRUMIT_TASKS_FILE
create table SCRUMIT_TASKS_FILE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TASK_ID uuid,
    FILE_ID uuid,
    --
    primary key (ID)
)^
-- end SCRUMIT_TASKS_FILE
-- begin SCRUMIT_TASK_DURATION
create table SCRUMIT_TASK_DURATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TASK_ID uuid,
    DATE_ date,
    DURATION integer,
    --
    primary key (ID)
)^
-- end SCRUMIT_TASK_DURATION
-- begin SCRUMIT_MEETINGS_TASK
create table SCRUMIT_MEETINGS_TASK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MEETING_ID uuid,
    TASK_ID uuid,
    COMMENT_ varchar(2048),
    --
    primary key (ID)
)^
-- end SCRUMIT_MEETINGS_TASK
-- begin SCRUMIT_CONTACTS_STATUS
create table SCRUMIT_CONTACTS_STATUS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    STATUS varchar(25),
    --
    primary key (ID)
)^
-- end SCRUMIT_CONTACTS_STATUS
-- begin SCRUMIT_CHAT_ROOM_PERFORMER_LINK
create table SCRUMIT_CHAT_ROOM_PERFORMER_LINK (
    CHAT_ROOM_ID uuid,
    PERFORMER_ID uuid,
    primary key (CHAT_ROOM_ID, PERFORMER_ID)
)^
-- end SCRUMIT_CHAT_ROOM_PERFORMER_LINK
-- begin SCRUMIT_COMMAND_TASK_LINK
create table SCRUMIT_COMMAND_TASK_LINK (
    TASK_ID uuid,
    COMMAND_ID uuid,
    primary key (TASK_ID, COMMAND_ID)
)^
-- end SCRUMIT_COMMAND_TASK_LINK
-- begin SCRUMIT_SPRINT_TASK_LINK
create table SCRUMIT_SPRINT_TASK_LINK (
    SPRINT_ID uuid,
    TASK_ID uuid,
    primary key (SPRINT_ID, TASK_ID)
)^
-- end SCRUMIT_SPRINT_TASK_LINK
-- begin SCRUMIT_TEAM_PERFORMER_LINK
create table SCRUMIT_TEAM_PERFORMER_LINK (
    TEAM_ID uuid,
    PERFORMER_ID uuid,
    primary key (TEAM_ID, PERFORMER_ID)
)^
-- end SCRUMIT_TEAM_PERFORMER_LINK
-- begin SEC_USER
alter table SEC_USER add column CONTACT_ID uuid ^
alter table SEC_USER add column DTYPE varchar(100) ^
update SEC_USER set DTYPE = 'sec$User' where DTYPE is null ^
-- end SEC_USER
-- begin SYS_FILE
alter table SYS_FILE add column DESCRIPTION varchar(255) ^
alter table SYS_FILE add column DTYPE varchar(100) ^
update SYS_FILE set DTYPE = 'sys$FileDescriptor' where DTYPE is null ^
-- end SYS_FILE
