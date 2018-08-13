alter table scrumit_complexity rename to SCRUMIT_COMPLEXITY__U40331 ;
alter table scrumit_estimation drop constraint FK_SCRUMIT_ESTIMATION_ON_COMPLEXITY ;
alter table scrumit_task drop constraint FK_SCRUMIT_TASK_ON_COMPLEXITY ;
