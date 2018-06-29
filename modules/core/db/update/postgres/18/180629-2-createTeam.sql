alter table SCRUMIT_TEAM add constraint FK_SCRUMIT_TEAM_ON_LEADER foreign key (LEADER_ID) references SEC_USER(ID);
create index IDX_SCRUMIT_TEAM_ON_LEADER on SCRUMIT_TEAM (LEADER_ID);
