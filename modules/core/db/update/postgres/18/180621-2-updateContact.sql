update SCRUMIT_CONTACT set FIO = '' where FIO is null ;
alter table SCRUMIT_CONTACT alter column FIO set not null ;
update SCRUMIT_CONTACT set EMAIL = '' where EMAIL is null ;
alter table SCRUMIT_CONTACT alter column EMAIL set not null ;
