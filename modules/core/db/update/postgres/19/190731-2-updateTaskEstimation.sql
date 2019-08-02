alter table SCRUMIT_TASK_ESTIMATION rename column value_ to value___u50057 ;
alter table SCRUMIT_TASK_ESTIMATION alter column value___u50057 drop not null ;
alter table SCRUMIT_TASK_ESTIMATION add column VALUE_ double precision ^
update SCRUMIT_TASK_ESTIMATION set VALUE_ = 0 where VALUE_ is null ;
alter table SCRUMIT_TASK_ESTIMATION alter column VALUE_ set not null ;
