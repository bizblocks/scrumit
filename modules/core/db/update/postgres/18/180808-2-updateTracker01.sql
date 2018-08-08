alter table SCRUMIT_TRACKER add constraint FK_SCRUMIT_TRACKER_ON_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_SCRUMIT_TRACKER_ON_FILE on SCRUMIT_TRACKER (FILE_ID);
