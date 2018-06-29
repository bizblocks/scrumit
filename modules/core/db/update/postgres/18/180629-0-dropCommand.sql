alter table scrumit_command rename to SCRUMIT_COMMAND__U99482 ;
alter table scrumit_command_task_link drop constraint FK_COMTAS_ON_COMMAND ;
alter table scrumit_sprint drop constraint FK_SCRUMIT_SPRINT_ON_COMMAND ;
alter table sec_user drop constraint FK_SEC_USER_ON_COMMAND ;
