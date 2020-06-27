alter table SCRUMIT_TASK rename column actual_time to actual_time__u31512 ;
alter table SCRUMIT_TASK rename column planning_time to planning_time__u63568 ;
alter table SCRUMIT_TASK add column RETURN_COMMENT text ;
alter table SCRUMIT_TASK add column START_WORK timestamp ;
alter table SCRUMIT_TASK add column TASK_CLASS_ID uuid ;
alter table SCRUMIT_TASK add column ABERANCE integer ;
alter table SCRUMIT_TASK add column PLANNING_TIME integer ;
alter table SCRUMIT_TASK add column ACTUAL_TIME integer ;
