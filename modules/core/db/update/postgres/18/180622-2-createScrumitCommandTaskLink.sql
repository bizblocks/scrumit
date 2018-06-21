alter table SCRUMIT_COMMAND_TASK_LINK add constraint FK_COMTAS_ON_TASK foreign key (TASK_ID) references SCRUMIT_TASK(ID);
alter table SCRUMIT_COMMAND_TASK_LINK add constraint FK_COMTAS_ON_COMMAND foreign key (COMMAND_ID) references SCRUMIT_COMMAND(ID);
