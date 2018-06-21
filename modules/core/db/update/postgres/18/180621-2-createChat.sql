alter table SCRUMIT_CHAT add constraint FK_SCRUMIT_CHAT_ON_SENDER foreign key (SENDER_ID) references SEC_USER(ID);
create index IDX_SCRUMIT_CHAT_ON_SENDER on SCRUMIT_CHAT (SENDER_ID);
