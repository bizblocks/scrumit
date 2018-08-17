alter table SYS_FILE add constraint FK_SYS_FILE_ON_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_SYS_FILE_ON_FILE on SYS_FILE (FILE_ID);
