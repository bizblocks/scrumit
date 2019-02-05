alter table SYS_FILE add constraint FK_SYS_FILE_ON_TRACKER foreign key (TRACKER_ID) references SCRUMIT_TRACKER(ID);
create index IDX_SYS_FILE_ON_TRACKER on SYS_FILE (TRACKER_ID);
