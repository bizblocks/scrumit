alter table SEC_USER rename column command_id to command_id__u77958 ;
drop index IDX_SEC_USER_ON_COMMAND ;
alter table SEC_USER drop constraint FK_SEC_USER_ON_COMMAND ;
