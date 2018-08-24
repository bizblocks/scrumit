alter table SCRUMIT_TASK rename column parent_bug_id to parent_bug_id__u91052 ;
drop index IDX_SCRUMIT_TASK_ON_PARENT_BUG ;
alter table SCRUMIT_TASK drop constraint FK_SCRUMIT_TASK_ON_PARENT_BUG ;
alter table SCRUMIT_TASK rename column tracker_id to tracker_id__u79284 ;
drop index IDX_SCRUMIT_TASK_ON_TRACKER ;
alter table SCRUMIT_TASK drop constraint FK_SCRUMIT_TASK_ON_TRACKER ;
