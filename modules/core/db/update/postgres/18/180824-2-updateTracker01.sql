alter table SCRUMIT_TRACKER add constraint FK_SCRUMIT_TRACKER_ON_TASK foreign key (TASK_ID) references SCRUMIT_TASK(ID);
