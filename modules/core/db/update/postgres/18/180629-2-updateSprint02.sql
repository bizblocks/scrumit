alter table SCRUMIT_SPRINT rename column command_id to command_id__u50307 ;
drop index IDX_SCRUMIT_SPRINT_ON_COMMAND ;
alter table SCRUMIT_SPRINT drop constraint FK_SCRUMIT_SPRINT_ON_COMMAND ;
alter table SCRUMIT_SPRINT add column TEAM_ID uuid ;
