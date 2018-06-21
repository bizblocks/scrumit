create table SCRUMIT_CHAT_ROOM_PERFORMER_LINK (
    CHAT_ROOM_ID uuid,
    PERFORMER_ID uuid,
    primary key (CHAT_ROOM_ID, PERFORMER_ID)
);
