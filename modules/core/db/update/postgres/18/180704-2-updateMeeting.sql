update SCRUMIT_MEETING set TYPE_ = 'SCRUM' where TYPE_ is null ;
alter table SCRUMIT_MEETING alter column TYPE_ set not null ;
update SCRUMIT_MEETING set DATE_ = current_date where DATE_ is null ;
alter table SCRUMIT_MEETING alter column DATE_ set not null ;
-- update SCRUMIT_MEETING set SPRINT_ID = <default_value> where SPRINT_ID is null ;
alter table SCRUMIT_MEETING alter column SPRINT_ID set not null ;
