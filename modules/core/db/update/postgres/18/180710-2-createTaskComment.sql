alter table SCRUMIT_TASK_COMMENT add constraint FK_SCRUMIT_TASK_COMMENT_ON_TASK foreign key (TASK_ID) references SCRUMIT_TASK(ID);
create index IDX_SCRUMIT_TASK_COMMENT_ON_TASK on SCRUMIT_TASK_COMMENT (TASK_ID);