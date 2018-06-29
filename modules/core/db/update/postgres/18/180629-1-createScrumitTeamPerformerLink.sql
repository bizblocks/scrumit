create table SCRUMIT_TEAM_PERFORMER_LINK (
    TEAM_ID uuid,
    PERFORMER_ID uuid,
    primary key (TEAM_ID, PERFORMER_ID)
);
