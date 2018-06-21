alter table SCRUMIT_LINK add constraint FK_SCRUMIT_LINK_ON_TASK foreign key (TASK_ID) references SCRUMIT_TASK(ID);
create index IDX_SCRUMIT_LINK_ON_TASK on SCRUMIT_LINK (TASK_ID);
