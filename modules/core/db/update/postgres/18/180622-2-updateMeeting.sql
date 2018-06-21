alter table SCRUMIT_MEETING rename column sprint_id to sprint_id__u50516 ;
drop index IDX_SCRUMIT_MEETING_ON_SPRINT ;
alter table SCRUMIT_MEETING add column SPRINT_ID uuid ;
