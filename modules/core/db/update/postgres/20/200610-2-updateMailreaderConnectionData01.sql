alter table MAILREADER_CONNECTION_DATA add constraint FK_MAILREADER_CONNECTION_DATA_PROJECT foreign key (PROJECT_ID) references SCRUMIT_TASK(ID);
