alter table SCRUMIT_FILES add constraint FK_SCRUMIT_FILES_ON_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_SCRUMIT_FILES_ON_FILE on SCRUMIT_FILES (FILE_ID);
