update SCRUMIT_CITY set NAME = '' where NAME is null ;
alter table SCRUMIT_CITY alter column NAME set not null ;
