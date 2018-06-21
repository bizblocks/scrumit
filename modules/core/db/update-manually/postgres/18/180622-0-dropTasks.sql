alter table scrumit_tasks rename to SCRUMIT_TASKS__U60586 ;
alter table scrumit_tasks drop constraint FK_SCRUMIT_TASKS_ON_TASK ;
alter table scrumit_tracker drop constraint FK_SCRUMIT_TRACKER_ON_TASK ;
