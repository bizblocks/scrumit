create table SCRUMIT_TASK_FILE_DESCRIPTOR_LINK (
    TASK_ID uuid,
    FILE_DESCRIPTOR_ID uuid,    
    primary key (TASK_ID, FILE_DESCRIPTOR_ID)
);
