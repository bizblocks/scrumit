alter table SCRUMIT_SPRINT add constraint FK_SCRUMIT_SPRINT_ON_TEAM foreign key (TEAM_ID) references SCRUMIT_TEAM(ID);
create index IDX_SCRUMIT_SPRINT_ON_TEAM on SCRUMIT_SPRINT (TEAM_ID);
