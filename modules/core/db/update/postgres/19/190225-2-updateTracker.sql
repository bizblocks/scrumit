alter table SCRUMIT_TRACKER rename column files_id to files_id__u50654 ;
drop index IDX_SCRUMIT_TRACKER_ON_FILES ;
alter table SCRUMIT_TRACKER drop constraint FK_SCRUMIT_TRACKER_ON_FILES ;
