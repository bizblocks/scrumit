alter table SYS_FILE rename column tracker_id to tracker_id__u58511 ;
drop index IDX_SYS_FILE_ON_TRACKER ;
alter table SYS_FILE drop constraint FK_SYS_FILE_ON_TRACKER ;
alter table SYS_FILE rename column file_id to file_id__u52205 ;
drop index IDX_SYS_FILE_ON_FILE ;
alter table SYS_FILE drop constraint FK_SYS_FILE_ON_FILE ;
alter table SYS_FILE rename column dtype to dtype__u57397 ;
alter table SYS_FILE rename column description to description__u62065 ;
