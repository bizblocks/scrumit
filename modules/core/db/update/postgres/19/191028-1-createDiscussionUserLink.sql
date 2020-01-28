create table SCRUMIT_DISCUSSION_USER_LINK (
    DISCUSSION_ID uuid,
    USER_ID uuid,
    primary key (DISCUSSION_ID, USER_ID)
);
