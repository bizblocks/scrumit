alter table SCRUMIT_TASK rename column tracker_id_id to tracker_id_id__u75338 ;
drop index IDX_SCRUMIT_TASK_ON_TRACKER_ID ;
alter table SCRUMIT_TASK drop constraint FK_SCRUMIT_TASK_ON_TRACKER_ID ;
