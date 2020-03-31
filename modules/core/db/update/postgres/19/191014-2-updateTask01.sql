alter table SCRUMIT_TASK add constraint FK_SCRUMIT_TASK_ON_PARENT foreign key (PARENT_ID) references SCRUMIT_TASK(ID);
create index IDX_SCRUMIT_TASK_ON_PARENT on SCRUMIT_TASK (PARENT_ID);
