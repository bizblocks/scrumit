alter table SCRUMIT_TRACKER add constraint FK_SCRUMIT_TRACKER_ON_WORKFLOW foreign key (WORKFLOW_ID) references WFSTP_WORKFLOW(ID);
create index IDX_SCRUMIT_TRACKER_ON_WORKFLOW on SCRUMIT_TRACKER (WORKFLOW_ID);
