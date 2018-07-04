alter table SCRUMIT_TASK rename column sprint_id to sprint_id__u31892 ;
drop index IDX_SCRUMIT_TASK_ON_SPRINT ;
alter table SCRUMIT_TASK drop constraint FK_SCRUMIT_TASK_ON_SPRINT ;
