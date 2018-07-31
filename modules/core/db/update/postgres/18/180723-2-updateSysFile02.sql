alter table SYS_FILE rename column tracker_id to tracker_id__u99456 ;
drop index IDX_SYS_FILE_ON_TRACKER ;
alter table SYS_FILE drop constraint FK_SYS_FILE_ON_TRACKER ;
