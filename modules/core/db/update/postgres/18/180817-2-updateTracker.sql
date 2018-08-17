alter table SCRUMIT_TRACKER rename column file_id to file_id__u43386 ;
drop index IDX_SCRUMIT_TRACKER_ON_FILE ;
alter table SCRUMIT_TRACKER drop constraint FK_SCRUMIT_TRACKER_ON_FILE ;
