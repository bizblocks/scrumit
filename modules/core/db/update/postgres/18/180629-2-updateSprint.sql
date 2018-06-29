alter table SCRUMIT_SPRINT rename column command_id to command_id__u42334 ;
drop index IDX_SCRUMIT_SPRINT_ON_COMMAND ;
alter table SCRUMIT_SPRINT add column COMMAND_ID uuid ;
