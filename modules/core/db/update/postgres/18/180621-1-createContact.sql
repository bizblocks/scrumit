create table SCRUMIT_CONTACT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FIO varchar(255),
    CITY_ID uuid,
    EMAIL varchar(255),
    PHONE varchar(255),
    COMMENTS varchar(255),
    --
    primary key (ID)
);
