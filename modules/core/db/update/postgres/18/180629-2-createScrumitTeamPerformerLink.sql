alter table SCRUMIT_TEAM_PERFORMER_LINK add constraint FK_TEAPER_ON_TEAM foreign key (TEAM_ID) references SCRUMIT_TEAM(ID);
alter table SCRUMIT_TEAM_PERFORMER_LINK add constraint FK_TEAPER_ON_PERFORMER foreign key (PERFORMER_ID) references SEC_USER(ID);
