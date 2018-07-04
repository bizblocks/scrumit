alter table SCRUMIT_SPRINT_TASK_LINK add constraint FK_SPRTAS_ON_SPRINT foreign key (SPRINT_ID) references SCRUMIT_SPRINT(ID);
alter table SCRUMIT_SPRINT_TASK_LINK add constraint FK_SPRTAS_ON_TASK foreign key (TASK_ID) references SCRUMIT_TASK(ID);
