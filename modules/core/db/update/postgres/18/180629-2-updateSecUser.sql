alter table SEC_USER rename column command_id to command_id__u58602 ;
drop index IDX_SEC_USER_ON_COMMAND ;
alter table SEC_USER add column COMMAND_ID uuid ;
