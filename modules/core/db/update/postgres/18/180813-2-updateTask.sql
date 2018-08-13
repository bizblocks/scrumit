alter table SCRUMIT_TASK rename column complexity_id to complexity_id__u94569 ;
drop index IDX_SCRUMIT_TASK_ON_COMPLEXITY ;
alter table SCRUMIT_TASK rename column duration to duration__u41159 ;
alter table SCRUMIT_TASK add column DURATION integer ;
