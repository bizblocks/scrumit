alter table SCRUMIT_TASK rename column actual_time to actual_time__u86372 ;
alter table SCRUMIT_TASK add column START_WORK timestamp ;
alter table SCRUMIT_TASK add column ACTUAL_TIME integer ;
