--这个脚本 目前只有两张表

create table D_OS_INFO  (
   OS_ID                varchar2(20)                    not null,
   OS_NAME              varchar2(200)                   not null,
   OS_URL               CHAR(10),
   DDE_SYNC_URL         CHAR(10),
   SYS_DATA_PUSH_OPTION CHAR(10),
   last_Modify_DATE   date,
   create_time        date,
   created            varchar2(8),
   constraint PK_D_OS_INFO primary key (OS_ID)
);
comment on column D_OS_INFO.DDE_SYNC_URL is
'这个仅供DDE使用';

create table D_DataBase_Info  (
   Database_Code      varchar2(32)                    not null,
   database_name      varchar2(100),
   OS_ID                varchar2(20),
   database_url       varchar2(1000),
   username           varchar2(100),
   password           varchar2(100),
   database_desc      varchar2(500),
   last_Modify_DATE   date,
   create_time        date,
   created            varchar2(8),
   constraint PK_D_DATABASE_INFO primary key (Database_Code)
);
comment on column D_DataBase_Info.password is
'加密';
alter table D_DataBase_Info
   add constraint FK_D_DATABA_REFERENCE_D_OS_INF foreign key (OS_ID)
      references D_OS_INFO (OS_ID);
