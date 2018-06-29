alter table SCRUMIT_TASK add constraint FK_SCRUMIT_TASK_ON_TOP foreign key (TOP_ID) references SCRUMIT_TASK(ID);
create index IDX_SCRUMIT_TASK_ON_TOP on SCRUMIT_TASK (TOP_ID);
