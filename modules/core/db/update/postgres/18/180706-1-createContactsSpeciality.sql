create table SCRUMIT_CONTACTS_SPECIALITY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CONTACT_ID uuid,
    SPECIALITY_ID uuid,
    --
    primary key (ID)
);
