alter table SEC_USER add constraint FK_SEC_USER_ON_COMMAND foreign key (COMMAND_ID) references SCRUMIT_TEAM(ID);
create index IDX_SEC_USER_ON_COMMAND on SEC_USER (COMMAND_ID);
