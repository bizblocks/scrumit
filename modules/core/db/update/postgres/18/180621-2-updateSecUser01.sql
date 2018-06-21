alter table SEC_USER add constraint FK_SEC_USER_ON_CONTACT foreign key (CONTACT_ID) references SCRUMIT_CONTACT(ID);
create index IDX_SEC_USER_ON_CONTACT on SEC_USER (CONTACT_ID);
