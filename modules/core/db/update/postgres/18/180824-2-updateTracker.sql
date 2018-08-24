alter table SCRUMIT_TRACKER rename column project_id to project_id__u08822 ;
drop index IDX_SCRUMIT_TRACKER_ON_PROJECT ;
alter table SCRUMIT_TRACKER drop constraint FK_SCRUMIT_TRACKER_ON_PROJECT ;
