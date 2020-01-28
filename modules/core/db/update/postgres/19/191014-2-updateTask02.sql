alter table SCRUMIT_TASK rename column parent_id to parent_id__u60896 ;
drop index IDX_SCRUMIT_TASK_ON_PARENT ;
alter table SCRUMIT_TASK drop constraint FK_SCRUMIT_TASK_ON_PARENT ;
