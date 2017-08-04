

DROP TABLE IF EXISTS F_ADDRESS_BOOK;

DROP TABLE IF EXISTS F_DATACATALOG;

DROP TABLE IF EXISTS F_DATADICTIONARY;

DROP TABLE IF EXISTS F_OPTDATASCOPE;

-- DROP INDEX IND_OPTID_OPTMETHOD ON F_OPTDEF;

DROP TABLE IF EXISTS F_OPTDEF;

-- DROP INDEX IND_TAG_ID ON F_OPT_LOG;

DROP TABLE IF EXISTS F_OPT_LOG;

DROP TABLE IF EXISTS F_OPTFLOWNOINFO;

DROP TABLE IF EXISTS F_OPTFLOWNOPOOL;

DROP TABLE IF EXISTS F_OPTINFO;

DROP TABLE IF EXISTS F_OPTINFODATA;

-- DROP INDEX IND_FILTER_TABLE_CLASS_NAME ON F_QUERY_FILTER_CONDITION;

DROP TABLE IF EXISTS F_QUERY_FILTER_CONDITION;

DROP TABLE IF EXISTS F_RANKGRANT;

DROP TABLE IF EXISTS F_ROLEINFO;

DROP TABLE IF EXISTS F_ROLEPOWER;

DROP TABLE IF EXISTS F_STAT_MONTH;

DROP TABLE IF EXISTS F_SYS_NOTIFY;

DROP TABLE IF EXISTS F_UNITINFO;

-- DROP INDEX IND_REGEMAIL ON F_USERINFO;

-- DROP INDEX IND_LOGINNAME ON F_USERINFO;

DROP TABLE IF EXISTS F_USERINFO;

DROP TABLE IF EXISTS F_USERROLE;

DROP TABLE IF EXISTS F_USERSETTING;

DROP TABLE IF EXISTS F_USERUNIT;

DROP TABLE IF EXISTS F_USER_FAVORITE;

-- DROP INDEX  IND_QUERY_FILTER_MODLE_CODE ON F_USER_QUERY_FILTER;

DROP TABLE IF EXISTS F_USER_QUERY_FILTER;

DROP TABLE IF EXISTS F_WORK_CLASS;

DROP TABLE IF EXISTS F_WORK_DAY;

DROP TABLE IF EXISTS M_INNERMSG;

DROP TABLE IF EXISTS M_INNERMSG_RECIPIENT;

DROP TABLE IF EXISTS M_MSGANNEX;

DROP TABLE IF EXISTS P_TASK_LIST;

/*==============================================================*/
/* TABLE: F_ADDRESS_BOOK                                        */
/*==============================================================*/
CREATE TABLE F_ADDRESS_BOOK
(
   ADDRBOOKID           NUMERIC(10,0) NOT NULL,
   BODYTYPE             VARCHAR(2) NOT NULL COMMENT '用户/个人/单位',
   BODYCODE             VARCHAR(16) NOT NULL COMMENT '用户/个人/单位 编号',
   REPRESENTATION       VARCHAR(200),
   UNITNAME             VARCHAR(200),
   DEPTNAME             VARCHAR(100),
   RANKNAME             VARCHAR(50),
   EMAIL                VARCHAR(60),
   EMAIL2               VARCHAR(60),
   EMAIL3               VARCHAR(60),
   HOMEPAGE             VARCHAR(100),
   QQ                   VARCHAR(20),
   MSN                  VARCHAR(60),
   WANGWANG             VARCHAR(20),
   BUZPHONE             VARCHAR(20),
   BUZPHONE2            VARCHAR(20),
   BUZFAX               VARCHAR(20),
   ASSIPHONE            VARCHAR(20),
   CALLBACPHONE         VARCHAR(20),
   CARPHONE             VARCHAR(20),
   UNITPHONE            VARCHAR(20),
   HOMEPHONE            VARCHAR(20),
   HOMEPHONE2           VARCHAR(20),
   HOMEPHONE3           VARCHAR(20),
   HOMEFAX              VARCHAR(20),
   MOBILEPHONE          VARCHAR(20),
   MOBILEPHONE2         VARCHAR(20),
   MOBILEPHONE3         VARCHAR(20),
   UNITZIP              VARCHAR(8),
   UNITPROVINCE         VARCHAR(20),
   UNITCITY             VARCHAR(20),
   UNITDISTRICT         VARCHAR(20),
   UNITSTREET           VARCHAR(20),
   UNITADDRESS          VARCHAR(60),
   HOMEZIP              VARCHAR(8),
   HOMEPROVINCE         VARCHAR(20),
   HOMECITY             VARCHAR(20),
   HOMEDISTRICT         VARCHAR(20),
   HOMESTREET           VARCHAR(20),
   HOMEADDRESS          VARCHAR(60),
   HOME2ZIP             VARCHAR(8),
   HOME2PROVINCE        VARCHAR(20),
   HOME2CITY            VARCHAR(20),
   HOME2DISTRICT        VARCHAR(20),
   HOME2STREET          VARCHAR(20),
   HOME2ADDRESS         VARCHAR(60),
   INUSEADDRESS         VARCHAR(1) COMMENT '单位/住宅/住宅2',
   SEARCHSTRING         VARCHAR(1000) COMMENT '前面各个字段的中文首字母，数字 连接的串',
   MEMO                 VARCHAR(500),
   LASTMODIFYDATE       DATE,
   CREATEDATE           DATE,
   PRIMARY KEY (ADDRBOOKID)
);

ALTER TABLE F_ADDRESS_BOOK COMMENT '系统中维持一个统一的通讯录 模块，主要目的是为了以后做 统一的接口，
比如：';

/*==============================================================*/
/* TABLE: F_DATACATALOG                                         */
/*==============================================================*/
CREATE TABLE F_DATACATALOG
(
   CATALOGCODE          VARCHAR(16) NOT NULL,
   CATALOGNAME          VARCHAR(64) NOT NULL,
   CATALOGSTYLE         CHAR(1) NOT NULL COMMENT 'F : 框架固有的 U:用户 S：系统  G国标',
   CATALOGTYPE          CHAR(1) NOT NULL COMMENT 'T：树状表格 L:列表 ',
   CATALOGDESC          VARCHAR(256),
   FIELDDESC            VARCHAR(1024) COMMENT '字段描述，不同字段用分号隔开',
   UPDATEDATE           DATE,
   CREATEDATE           DATE,
   OPTID                VARCHAR(16) COMMENT '业务分类，使用数据字典DICTIONARYTYPE中数据',
   NEEDCACHE            CHAR(1) DEFAULT '1',
   CREATOR              VARCHAR(32),
   UPDATOR              VARCHAR(32),
   PRIMARY KEY (CATALOGCODE)
);


ALTER TABLE F_DATACATALOG COMMENT '类别状态	 U:用户 S：系统，G国标
类别形式  T：树状表格 L:列表
';


/*==============================================================*/
/* TABLE: F_DATADICTIONARY                                      */
/*==============================================================*/
CREATE TABLE F_DATADICTIONARY
(
   CATALOGCODE          VARCHAR(16) NOT NULL,
   DATACODE             VARCHAR(16) NOT NULL,
   EXTRACODE            VARCHAR(16) COMMENT '树型字典的父类代码',
   EXTRACODE2           VARCHAR(16) COMMENT '默认的排序字段',
   DATATAG              CHAR(1) COMMENT 'N正常，D已停用，用户可以自解释这个字段',
   DATAVALUE            VARCHAR(2048),
   DATASTYLE            CHAR(1) COMMENT 'F : 框架固有的 U:用户 S：系统  G国标',
   DATADESC             VARCHAR(256),
   LASTMODIFYDATE       DATE,
   CREATEDATE           DATE,
   DATAORDER            NUMERIC(6,0) COMMENT '排序字段',
   PRIMARY KEY (CATALOGCODE, DATACODE)
);

ALTER TABLE F_DATADICTIONARY COMMENT '数据字典：存放一些常量数据 比如出物提示信息，还有一些 代码与名称的对应表，比如 状态，角色名，头衔 等等
';

/*==============================================================*/
/* TABLE: F_OPTDATASCOPE                                        */
/*==============================================================*/
CREATE TABLE F_OPTDATASCOPE
(
   OPTSCOPECODE         VARCHAR(16) NOT NULL,
   OPTID                VARCHAR(16),
   SCOPENAME            VARCHAR(64),
   FILTERCONDITION      VARCHAR(1024) COMMENT '条件语句，可以有的参数 [MT] 业务表 [UC] 用户代码 [UU] 用户机构代码',
   SCOPEMEMO            VARCHAR(1024) COMMENT '数据权限说明',
   FILTERGROUP          VARCHAR(16) DEFAULT 'G',
   PRIMARY KEY (OPTSCOPECODE)
);

/*==============================================================*/
/* TABLE: F_OPTDEF                                              */
/*==============================================================*/
CREATE TABLE F_OPTDEF
(
   OPTCODE              VARCHAR(32) NOT NULL,
   OPTID                VARCHAR(32),
   OPTNAME              VARCHAR(100),
   OPTMETHOD            VARCHAR(50) COMMENT '操作参数 方法',
   OPTURL               VARCHAR(256),
   OPTDESC              VARCHAR(256),
   ISINWORKFLOW         CHAR(1) COMMENT '是否为流程操作方法 F：不是  T ： 是',
   UPDATEDATE           DATE,
   CREATEDATE           DATE,
   OPTREQ               VARCHAR(8),
   OPTORDER 			NUMERIC(4),
   CREATOR              VARCHAR(32),
   UPDATOR              VARCHAR(32),
   PRIMARY KEY (OPTCODE)
);

/*==============================================================*/
/* INDEX: IND_OPTID_OPTMETHOD                                   */
/*==============================================================*/
CREATE INDEX IND_OPTID_OPTMETHOD ON F_OPTDEF
(
   OPTID,
   OPTMETHOD
);

/*==============================================================*/
/* TABLE: F_OPT_LOG                                             */
/*==============================================================*/
CREATE TABLE F_OPT_LOG
(
   LOGID                NUMERIC(12,0) NOT NULL,
   LOGLEVEL             VARCHAR(2) NOT NULL,
   USERCODE             VARCHAR(8) NOT NULL,
   OPTTIME              DATE NOT NULL,
   OPTCONTENT           VARCHAR(1000) NOT NULL COMMENT '操作描述',
   NEWVALUE             TEXT COMMENT '新值',
   OLDVALUE             TEXT COMMENT '原值',
   OPTID                VARCHAR(64) NOT NULL COMMENT '模块，或者表',
   OPTMETHOD            VARCHAR(64) COMMENT '方法，或者字段',
   OPTTAG               VARCHAR(200) COMMENT '一般用于关联到业务主体的标识、表的主键等等',
   PRIMARY KEY (LOGID)
);

/*==============================================================*/
/* INDEX: IND_TAG_ID                                            */
/*==============================================================*/
CREATE INDEX IND_TAG_ID ON F_OPT_LOG
(
   OPTTAG
);

/*==============================================================*/
/* TABLE: F_OPTFLOWNOINFO                                       */
/*==============================================================*/
CREATE TABLE F_OPTFLOWNOINFO
(
   OWNERCODE            VARCHAR(8) NOT NULL,
   CODECODE             VARCHAR(16) NOT NULL,
   CODEDATE             DATE NOT NULL,
   CURNO                NUMERIC(6,0) NOT NULL DEFAULT 1,
   LASTCODEDATE         DATE,
   CREATEDATE           DATE,
   LASTMODIFYDATE       DATE,
   PRIMARY KEY (OWNERCODE, CODEDATE, CODECODE)
);

/*==============================================================*/
/* TABLE: F_OPTFLOWNOPOOL                                       */
/*==============================================================*/
CREATE TABLE F_OPTFLOWNOPOOL
(
   OWNERCODE            VARCHAR(8) NOT NULL,
   CODECODE             VARCHAR(16) NOT NULL,
   CODEDATE             DATE NOT NULL,
   CURNO                NUMERIC(6,0) NOT NULL DEFAULT 1,
   CREATEDATE           DATE,
   PRIMARY KEY (OWNERCODE, CODEDATE, CODECODE, CURNO)
);

/*==============================================================*/
/* TABLE: F_OPTINFO                                             */
/*==============================================================*/
CREATE TABLE F_OPTINFO
(
   OPTID                VARCHAR(32) NOT NULL,
   OPTNAME              VARCHAR(100) NOT NULL,
   PREOPTID             VARCHAR(32) NOT NULL,
   OPTROUTE             VARCHAR(256) COMMENT '与ANGULARJS路由匹配',
   OPTURL               VARCHAR(256),
   FORMCODE             VARCHAR(4),
   OPTTYPE              CHAR(1) COMMENT ' S:实施业务, O:普通业务, W:流程业务, I :项目业务',
   MSGNO                NUMERIC(10,0),
   MSGPRM               VARCHAR(256),
   ISINTOOLBAR          CHAR(1),
   IMGINDEX             NUMERIC(10,0),
   TOPOPTID             VARCHAR(8),
   ORDERIND             NUMERIC(4,0) COMMENT '这个顺序只需在同一个父业务下排序',
   FLOWCODE             VARCHAR(8) COMMENT '同一个代码的流程应该只有一个有效的版本',
   PAGETYPE             CHAR(1) NOT NULL DEFAULT 'I' COMMENT 'D : DIV I:IFRAME',
   ICON                 VARCHAR(512),
   HEIGHT               NUMERIC(10,0),
   WIDTH                NUMERIC(10,0),
   UPDATEDATE           DATE,
   CREATEDATE           DATE,
   CREATOR              VARCHAR(32),
   UPDATOR              VARCHAR(32),
   PRIMARY KEY (OPTID)
);

/*==============================================================*/
/* TABLE: F_OPTINFODATA                                         */
/*==============================================================*/
CREATE TABLE F_OPTINFODATA
(
   TBCODE               VARCHAR(32) NOT NULL,
   OPTID                VARCHAR(8) NOT NULL,
   LASTMODIFYDATE       DATE,
   CREATEDATE           DATE,
   PRIMARY KEY (TBCODE, OPTID)
);

ALTER TABLE F_OPTINFODATA COMMENT '业务模块和表是多对多的关系,这个表仅仅是作为数据权限设置时的一个辅助表的';

/*==============================================================*/
/* TABLE: F_QUERY_FILTER_CONDITION                              */
/*==============================================================*/
CREATE TABLE F_QUERY_FILTER_CONDITION
(
   CONDITION_NO         NUMERIC(12,0) NOT NULL,
   TABLE_CLASS_NAME     VARCHAR(64) NOT NULL COMMENT '数据库表代码或者PO的类名',
   PARAM_NAME           VARCHAR(64) NOT NULL COMMENT '参数名',
   PARAM_LABEL          VARCHAR(120) NOT NULL COMMENT '参数输入框提示',
   PARAM_TYPE           VARCHAR(8) COMMENT '参数类型：S 字符串，L 数字， N 有小数点数据， D 日期， T 时间戳， Y 年， M 月',
   DEFAULT_VALUE        VARCHAR(100),
   FILTER_SQL           VARCHAR(200) COMMENT '过滤语句，将会拼装到SQL语句中',
   SELECT_DATA_TYPE     CHAR(1) NOT NULL DEFAULT 'N' COMMENT '数据下拉框内容； N ：没有， D 数据字典, S 通过SQL语句获得， J JSON数据直接获取
            ',
   SELECT_DATA_CATALOG  VARCHAR(64) COMMENT '数据字典',
   SELECT_SQL           VARCHAR(1000) COMMENT '有两个返回字段的SQL语句',
   SELECT_JSON          VARCHAR(2000) COMMENT 'KEY,VALUE数值对，JSON格式',
   PRIMARY KEY (CONDITION_NO)
);

/*==============================================================*/
/* INDEX: IND_FILTER_TABLE_CLASS_NAME                           */
/*==============================================================*/
CREATE INDEX IND_FILTER_TABLE_CLASS_NAME ON F_QUERY_FILTER_CONDITION
(
   TABLE_CLASS_NAME
);

/*==============================================================*/
/* TABLE: F_RANKGRANT                                           */
/*==============================================================*/
CREATE TABLE F_RANKGRANT
(
   RANK_GRANT_ID        NUMERIC(12,0) NOT NULL,
   GRANTER              VARCHAR(8) NOT NULL,
   UNITCODE             VARCHAR(6) NOT NULL,
   USERSTATION          VARCHAR(4) NOT NULL,
   USERRANK             VARCHAR(2) NOT NULL COMMENT 'RANK 代码不是 0开头的可以进行授予',
   BEGINDATE            DATE NOT NULL,
   GRANTEE              VARCHAR(8) NOT NULL,
   ENDDATE              DATE,
   GRANTDESC            VARCHAR(256),
   LASTMODIFYDATE       DATE,
   CREATEDATE           DATE,
   PRIMARY KEY (RANK_GRANT_ID, USERRANK)
);

/*==============================================================*/
/* TABLE: F_ROLEINFO                                            */
/*==============================================================*/
CREATE TABLE F_ROLEINFO
(
   ROLECODE             VARCHAR(32) NOT NULL,
   ROLENAME             VARCHAR(64),
   ROLETYPE             CHAR(1) NOT NULL COMMENT 'S为系统功能角色 I 为项目角色 W工作量角色',
   UNITCODE             VARCHAR(32),
   ISVALID              CHAR(1) NOT NULL,
   ROLEDESC             VARCHAR(256),
   UPDATEDATE           DATE,
   CREATEDATE           DATE,
   CREATOR              VARCHAR(32),
   UPDATOR              VARCHAR(32),
   PRIMARY KEY (ROLECODE)
);

/*==============================================================*/
/* TABLE: F_ROLEPOWER                                           */
/*==============================================================*/
CREATE TABLE F_ROLEPOWER
(
   ROLECODE             VARCHAR(32) NOT NULL,
   OPTCODE              VARCHAR(32) NOT NULL,
   OPTSCOPECODES        VARCHAR(1000) COMMENT '用逗号隔开的数据范围结合（空\ALL 表示全部）',
   UPDATEDATE           DATE,
   CREATEDATE           DATE,
   CREATOR              VARCHAR(32),
   UPDATOR              VARCHAR(32),
   PRIMARY KEY (ROLECODE, OPTCODE)
);

/*==============================================================*/
/* TABLE: F_STAT_MONTH                                          */
/*==============================================================*/
CREATE TABLE F_STAT_MONTH
(
   YEARMONTH            VARCHAR(6) NOT NULL COMMENT 'YYYYMM',
   BEGINDAY             DATE NOT NULL,
   EENDDAY              DATE NOT NULL,
   ENDSCHEDULE          CHAR(1) COMMENT '这个字段忽略',
   BEGINSCHEDULE        CHAR(1) COMMENT '这个字段忽略',
   PRIMARY KEY (YEARMONTH)
);

ALTER TABLE F_STAT_MONTH COMMENT 'OA业务统计月，可以自定义统计月的起止日期';

/*==============================================================*/
/* TABLE: F_SYS_NOTIFY                                          */
/*==============================================================*/
CREATE TABLE F_SYS_NOTIFY
(
   NOTIFY_ID            NUMERIC(12,0) NOT NULL,
   NOTIFY_SENDER        VARCHAR(100),
   NOTIFY_RECEIVER      VARCHAR(100) NOT NULL,
   MSG_SUBJECT          VARCHAR(200),
   MSG_CONTENT          VARCHAR(2000) NOT NULL,
   NOTICE_TYPE          VARCHAR(100),
   NOTIFY_STATE         CHAR(1) COMMENT '0 成功， 1 失败 2 部分成功',
   ERROR_MSG            VARCHAR(500),
   NOTIFY_TIME          DATE,
   OPTTAG               VARCHAR(200) COMMENT '一般用于关联到业务主体',
   OPTMETHOD            VARCHAR(64) COMMENT '方法，或者字段',
   OPTID                VARCHAR(64) NOT NULL COMMENT '模块，或者表',
   PRIMARY KEY (NOTIFY_ID)
);

/*==============================================================*/
/* TABLE: F_UNITINFO                                            */
/*==============================================================*/
CREATE TABLE F_UNITINFO
(
   UNITCODE             VARCHAR(32) NOT NULL,
   PARENTUNIT           VARCHAR(32),
   UNITTYPE             CHAR(1) COMMENT '发布任务/ 邮电规划/组队/接收任务',
   ISVALID              CHAR(1) NOT NULL COMMENT 'T:生效 F:无效',
   UNITTAG              VARCHAR(100) COMMENT '用户第三方系统管理',
   UNITNAME             VARCHAR(300) NOT NULL,
   ENGLISHNAME          VARCHAR(300),
   DEPNO                VARCHAR(100) COMMENT '组织机构代码：',
   UNITDESC             VARCHAR(256),
   ADDRBOOKID           NUMERIC(10,0),
   UNITSHORTNAME        VARCHAR(32),
   UNITWORD             VARCHAR(100),
   UNITGRADE            NUMERIC(4,0),
   UNITORDER            NUMERIC(4,0),
   UPDATEDATE           DATE,
   CREATEDATE           DATE,
   EXTJSONINFO          VARCHAR(1000),
   CREATOR              VARCHAR(32),
   UPDATOR              VARCHAR(32),
   UNITPATH 			VARCHAR(1000),
   UNITMANAGER VARCHAR(32),
   PRIMARY KEY (UNITCODE)
);

/*==============================================================*/
/* TABLE: F_USERINFO                                            */
/*==============================================================*/
CREATE TABLE F_USERINFO
(
   USERCODE             VARCHAR(32) NOT NULL,
   USERPIN              VARCHAR(32),
   USERTYPE             CHAR(1) DEFAULT 'U' COMMENT '发布任务/接收任务/系统管理',
   ISVALID              CHAR(1) NOT NULL COMMENT 'T:生效 F:无效',
   LOGINNAME            VARCHAR(100) NOT NULL,
   USERNAME             VARCHAR(300) NOT NULL COMMENT '昵称',
   USERTAG              VARCHAR(100) COMMENT '用于第三方系统关联',
   ENGLISHNAME          VARCHAR(300),
   USERDESC             VARCHAR(256),
   LOGINTIMES           NUMERIC(6,0),
   ACTIVETIME           DATE,
   LOGINIP              VARCHAR(16),
   ADDRBOOKID           NUMERIC(10,0),
   REGEMAIL             VARCHAR(60) COMMENT '注册用EMAIL，不能重复',
   USERPWD              VARCHAR(20) COMMENT '如果需要可以有',
   PWDEXPIREDTIME       DATE,
   REGCELLPHONE         VARCHAR(15),
   PRIMARYUNIT          VARCHAR(32),
   USERWORD             VARCHAR(100) COMMENT '微信号',
   USERORDER            NUMERIC(4,0),
   UPDATEDATE           DATE,
   CREATEDATE           DATE,
   EXTJSONINFO          VARCHAR(1000),
   CREATOR              VARCHAR(32),
   UPDATOR              VARCHAR(32),
   PRIMARY KEY (USERCODE)
);

/*==============================================================*/
/* INDEX: IND_LOGINNAME                                         */
/*==============================================================*/
CREATE UNIQUE INDEX IND_LOGINNAME ON F_USERINFO
(
   LOGINNAME
);

/*==============================================================*/
/* INDEX: IND_REGEMAIL                                          */
/*==============================================================*/
CREATE UNIQUE INDEX IND_REGEMAIL ON F_USERINFO
(
   REGEMAIL
);

/*==============================================================*/
/* TABLE: F_USERROLE                                            */
/*==============================================================*/
CREATE TABLE F_USERROLE
(
   USERCODE             VARCHAR(32) NOT NULL,
   ROLECODE             VARCHAR(32) NOT NULL,
   OBTAINDATE           DATE NOT NULL,
   SECEDEDATE           DATE,
   CHANGEDESC           VARCHAR(256),
   UPDATEDATE           DATE,
   CREATEDATE           DATE,
   CREATOR              VARCHAR(32),
   UPDATOR              VARCHAR(32),
   PRIMARY KEY (USERCODE, ROLECODE)
);

/*==============================================================*/
/* TABLE: F_USERSETTING                                         */
/*==============================================================*/
CREATE TABLE F_USERSETTING
(
   USERCODE             VARCHAR(8) NOT NULL COMMENT 'DEFAULT:为默认设置
            SYS001~SYS999: 为系统设置方案
            是一个用户号,或者是系统的一个设置方案',
   PARAMCODE            VARCHAR(16) NOT NULL,
   PARAMVALUE           VARCHAR(2048) NOT NULL,
   OPTID                VARCHAR(16) NOT NULL,
   PARAMNAME            VARCHAR(200),
   CREATEDATE           DATE,
   PRIMARY KEY (USERCODE, PARAMCODE)
);

/*==============================================================*/
/* TABLE: F_USERUNIT                                            */
/*==============================================================*/
CREATE TABLE F_USERUNIT
(
   USERUNITID           VARCHAR(16) NOT NULL,
   UNITCODE             VARCHAR(6) NOT NULL,
   USERCODE             VARCHAR(8) NOT NULL,
   ISPRIMARY            CHAR(1) NOT NULL DEFAULT '1' COMMENT 'T：为主， F：兼职',
   USERSTATION          VARCHAR(16) NOT NULL,
   USERRANK             VARCHAR(2) NOT NULL COMMENT 'RANK 代码不是 0开头的可以进行授予',
   RANKMEMO             VARCHAR(256) COMMENT '任职备注',
   USERORDER            NUMERIC(8,0) DEFAULT 0,
   UPDATEDATE           DATE,
   CREATEDATE           DATE,
   CREATOR              VARCHAR(32),
   UPDATOR              VARCHAR(32),
   PRIMARY KEY (USERUNITID)
);

ALTER TABLE F_USERUNIT COMMENT '同一个人可能在多个部门担任不同的职位';

/*==============================================================*/
/* TABLE: F_USER_FAVORITE                                       */
/*==============================================================*/
CREATE TABLE F_USER_FAVORITE
(
   USERCODE             VARCHAR(8) NOT NULL COMMENT 'DEFAULT:为默认设置
            SYS001~SYS999: 为系统设置方案
            是一个用户号,或者是系统的一个设置方案',
   OPTID                VARCHAR(16) NOT NULL,
   LASTMODIFYDATE       DATE,
   CREATEDATE           DATE,
   PRIMARY KEY (USERCODE, OPTID)
);

/*==============================================================*/
/* TABLE: F_USER_QUERY_FILTER                                   */
/*==============================================================*/
CREATE TABLE F_USER_QUERY_FILTER
(
   FILTER_NO            NUMERIC(12,0) NOT NULL,
   USERCODE             VARCHAR(8) NOT NULL,
   MODLE_CODE           VARCHAR(64) NOT NULL COMMENT '开发人员自行定义，单不能重复，建议用系统的模块名加上当前的操作方法',
   FILTER_NAME          VARCHAR(200) NOT NULL COMMENT '用户自行定义的名称',
   FILTER_VALUE         VARCHAR(3200) NOT NULL COMMENT '变量值，JSON格式，对应一个MAP',
   PRIMARY KEY (FILTER_NO)
);

/*==============================================================*/
/* INDEX: IND_QUERY_FILTER_MODLE_CODE                           */
/*==============================================================*/
CREATE INDEX IND_QUERY_FILTER_MODLE_CODE ON F_USER_QUERY_FILTER
(
   MODLE_CODE
);

/*==============================================================*/
/* TABLE: F_WORK_CLASS                                          */
/*==============================================================*/
CREATE TABLE F_WORK_CLASS
(
   CLASS_ID             NUMERIC(12,0) NOT NULL,
   CLASS_NAME           VARCHAR(50) NOT NULL,
   SHORT_NAME           VARCHAR(10) NOT NULL,
   BEGIN_TIME           VARCHAR(6) COMMENT '9:00',
   END_TIME             VARCHAR(6) COMMENT '+4:00 ''+''表示第二天',
   HAS_BREAK            CHAR(1),
   BREAK_BEGIN_TIME     VARCHAR(6) COMMENT '9:00',
   BREAK_END_TIME       VARCHAR(6) COMMENT '+4:00 ''+''表示第二天',
   CLASS_DESC           VARCHAR(500),
   RECORD_DATE          DATE,
   RECORDER             VARCHAR(8),
   PRIMARY KEY (CLASS_ID)
);

ALTER TABLE F_WORK_CLASS COMMENT 'CLASS_ID
 为 0 的表示休息，可以不在这个表中出现
 为 1 的为默认班次信息';

/*==============================================================*/
/* TABLE: F_WORK_DAY                                            */
/*==============================================================*/
CREATE TABLE F_WORK_DAY
(
   WORKDAY              DATE NOT NULL,
   DAYTYPE              CHAR(1) NOT NULL COMMENT 'A:工作日放假，B:周末调休成工作时间 C 正常上班 D正常休假',
   WORKTIMETYPE         VARCHAR(20),
   WORKDAYDESC          VARCHAR(255),
   PRIMARY KEY (WORKDAY)
);

ALTER TABLE F_WORK_DAY COMMENT '非正常作业时间日
A:工作日放假 B:周末调休成工作时间  C: 正常上班  D:正常休假
';

/*==============================================================*/
/* TABLE: M_INNERMSG                                            */
/*==============================================================*/
CREATE TABLE M_INNERMSG
(
   MSGCODE              VARCHAR(16) NOT NULL COMMENT '消息主键自定义，通过S_M_INNERMSG序列生成',
   SENDER               VARCHAR(128),
   SENDDATE             DATE,
   MSGTITLE             VARCHAR(128),
   MSGTYPE              CHAR(1) COMMENT 'P= 个人为消息  A= 机构为公告（通知）
            M=邮件',
   MAILTYPE             CHAR(1) COMMENT 'I=收件箱
            O=发件箱
            D=草稿箱
            T=废件箱


            ',
   MAILUNDELTYPE        CHAR(1),
   RECEIVENAME          VARCHAR(2048) COMMENT '使用部门，个人中文名，中间使用英文分号分割',
   HOLDUSERS            NUMERIC(8,0) COMMENT '总数为发送人和接收人数量相加，发送和接收人删除消息时-1，当数量为0时真正删除此条记录

            消息类型为邮件时不需要设置',
   MSGSTATE             CHAR(1) COMMENT '未读/已读/删除',
   MSGCONTENT           LONGBLOB,
   EMAILID              VARCHAR(8) COMMENT '用户配置多邮箱时使用',
   OPTID                VARCHAR(64) NOT NULL COMMENT '模块，或者表',
   OPTMETHOD            VARCHAR(64) COMMENT '方法，或者字段',
   OPTTAG               VARCHAR(200) COMMENT '一般用于关联到业务主体',
   PRIMARY KEY (MSGCODE)
);

ALTER TABLE M_INNERMSG COMMENT '内部消息与公告
接受代码,  其实可以独立出来, 因为他 和发送人 是 一对多的关系

                               -&#';

/*==============================================================*/
/* TABLE: M_INNERMSG_RECIPIENT                                  */
/*==============================================================*/
CREATE TABLE M_INNERMSG_RECIPIENT
(
   MSGCODE              VARCHAR(16) NOT NULL,
   RECEIVE              VARCHAR(8) NOT NULL,
   REPLYMSGCODE         INT,
   RECEIVETYPE          CHAR(1) COMMENT 'P=个人为消息
            A=机构为公告
            M=邮件',
   MAILTYPE             CHAR(1) COMMENT 'T=收件人
            C=抄送
            B=密送',
   MSGSTATE             CHAR(1) COMMENT '未读/已读/删除，收件人在线时弹出提示

            U=未读
            R=已读
            D=删除',
   ID                   VARCHAR(16) NOT NULL,
   PRIMARY KEY (ID)
);

ALTER TABLE M_INNERMSG_RECIPIENT COMMENT '内部消息（邮件）与公告收件人及消息信息';

/*==============================================================*/
/* TABLE: M_MSGANNEX                                            */
/*==============================================================*/
CREATE TABLE M_MSGANNEX
(
   MSGCODE              VARCHAR(16) NOT NULL,
   INFOCODE             VARCHAR(16) NOT NULL,
   MSGANNEXID           VARCHAR(16) NOT NULL,
   PRIMARY KEY (MSGANNEXID)
);

/*==============================================================*/
/* TABLE: P_TASK_LIST                                           */
/*==============================================================*/
CREATE TABLE P_TASK_LIST
(
   TASKID               NUMERIC(12,0) NOT NULL COMMENT '自动生成的主键，需要一个序列来配合',
   TASKOWNER            VARCHAR(8) NOT NULL COMMENT '谁的任务',
   TASKTAG              VARCHAR(1) NOT NULL COMMENT '类似与OUTLOOK中的邮件标记，可以用不同的颜色的旗子图表标识',
   TASKRANK             VARCHAR(1) NOT NULL COMMENT '任务的优先级',
   TASKSTATUS           VARCHAR(2) NOT NULL COMMENT '处理中、完成、取消、终止',
   TASKTITLE            VARCHAR(256) NOT NULL,
   TASKMEMO             VARCHAR(1000) COMMENT '简要描述任务的具体内容',
   TASKTYPE             VARCHAR(8) NOT NULL COMMENT '个人、组织活动、领导委派 等等',
   OPTID                VARCHAR(64) NOT NULL COMMENT '模块，或者表',
   OPTMETHOD            VARCHAR(64) COMMENT '方法，或者字段',
   OPTTAG               VARCHAR(200) COMMENT '一般用于关联到业务主体',
   CREATOR              VARCHAR(8) NOT NULL,
   CREATED              DATE NOT NULL,
   PLANBEGINTIME        DATE NOT NULL,
   PLANENDTIME          DATE,
   BEGINTIME            DATE,
   ENDTIME              DATE,
   FINISHMEMO           VARCHAR(1000) COMMENT '简要记录任务的执行过程和结果',
   NOTICESIGN           VARCHAR(1) COMMENT '提醒标志为：禁止提醒、未提醒、已提醒',
   LASTNOTICETIME       DATE COMMENT '最后一次提醒时间，根据提醒策略可以提醒多次',
   TASKDEADLINE         DATE,
   TASKVALUE            VARCHAR(2048) COMMENT '备用，字段不够时使用',
   PRIMARY KEY (TASKID)
);

DROP FUNCTION IF EXISTS calcUnitPath;

DELIMITER //
-- 重新计算 untiPath 算法，系统中并不需要这个函数，这个函数是供实施人员手动执行的
CREATE FUNCTION calcUnitPath (chrId varchar(32))
   RETURNS varchar(1000)
   BEGIN
      DECLARE sTemp VARCHAR(32);
      DECLARE sPreTemp VARCHAR(32);
      DECLARE path VARCHAR(1000);
      DECLARE rs VARCHAR(1000);
      SET  sTemp = trim(chrId);
      SET  path = '';
      REPEAT
         SET  path = concat('/',sTemp, path);
         set sPreTemp = sTemp;
         SELECT unitcode INTO sTemp
         FROM f_unitinfo
         where unitcode =
               (select parentunit FROM f_unitinfo where unitcode = sTemp);
      until sTemp is null or sTemp='' or sPreTemp = sTemp
      END REPEAT;
      RETURN path;
   END //

DELIMITER ;

-- V_HI_UNITINFO视图脚本

CREATE OR REPLACE VIEW V_HI_UNITINFO AS
SELECT A.UNITCODE AS TOPUNITCODE,  B.UNITCODE,B.UNITTYPE, B.PARENTUNIT, B.ISVALID,     B.UNITNAME,B.UNITDESC,B.UNITSHORTNAME,B.ADDRBOOKID,B.UNITORDER,B.DEPNO,
       B.UNITWORD,B.UNITGRADE,
       LENGTH(B.UNITPATH)- LENGTH(REPLACE(B.UNITPATH,'/','')) - LENGTH(A.UNITPATH) + LENGTH(REPLACE(A.UNITPATH,'/',''))+1  AS HI_LEVEL,
       SUBSTR(B.UNITPATH ,  LENGTH(A.UNITPATH)+1) AS UNITPATH
  FROM F_UNITINFO A , F_UNITINFO B
 WHERE B.UNITPATH LIKE CONCAT(A.UNITPATH,'%' );


CREATE OR REPLACE VIEW F_V_OPT_ROLE_MAP AS
SELECT CONCAT(C.OPTURL, B.OPTURL) AS OPTURL, B.OPTREQ, A.ROLECODE, C.OPTID, B.OPTCODE
  FROM F_ROLEPOWER A
  JOIN F_OPTDEF B
    ON (A.OPTCODE = B.OPTCODE)
  JOIN F_OPTINFO C
    ON (B.OPTID = C.OPTID)
 WHERE C.OPTTYPE <> 'W'
   AND C.OPTURL <> '...'
 ORDER BY C.OPTURL, B.OPTREQ, A.ROLECODE;
/*==============================================================*/
/* VIEW: F_V_USERROLES                                          */
/*==============================================================*/
CREATE OR REPLACE VIEW F_V_USERROLES AS
SELECT DISTINCT B.ROLECODE,B.ROLENAME,B.ISVALID,B.ROLEDESC,B.CREATEDATE,B.UPDATEDATE ,A.USERCODE
    FROM F_USERROLE A JOIN F_ROLEINFO B ON (A.ROLECODE=B.ROLECODE)
    WHERE A.OBTAINDATE <=  NOW() AND (A.SECEDEDATE IS NULL OR A.SECEDEDATE > NOW()) AND B.ISVALID='T'
UNION ALL
  SELECT D.ROLECODE,D.ROLENAME,D.ISVALID,D.ROLEDESC,D.CREATEDATE,D.UPDATEDATE , C.USERCODE
   FROM F_USERINFO C , F_ROLEINFO D
   WHERE D.ROLECODE = 'G-PUBLIC';

/*==============================================================*/
/* VIEW: F_V_USEROPTDATASCOPES                                  */
/*==============================================================*/
CREATE OR REPLACE VIEW F_V_USEROPTDATASCOPES AS
SELECT  DISTINCT A.USERCODE, C. OPTID ,  C.OPTMETHOD , B.OPTSCOPECODES
FROM F_V_USERROLES A  JOIN F_ROLEPOWER   B ON (A.ROLECODE=B.ROLECODE)
         JOIN F_OPTDEF  C ON(B.OPTCODE=C.OPTCODE);
/*==============================================================*/
/* VIEW: F_V_USEROPTLIST                                        */
/*==============================================================*/
CREATE OR REPLACE VIEW F_V_USEROPTLIST AS
SELECT  DISTINCT A.USERCODE,  C.OPTCODE,  C.OPTNAME  ,  C. OPTID ,  C.OPTMETHOD
FROM F_V_USERROLES A  JOIN F_ROLEPOWER   B ON (A.ROLECODE=B.ROLECODE)
         JOIN F_OPTDEF  C ON(B.OPTCODE=C.OPTCODE);

/*==============================================================*/
/* VIEW: F_V_USEROPTMOUDLELIST                                  */
/*==============================================================*/

CREATE OR REPLACE VIEW F_V_USEROPTMOUDLELIST AS
SELECT  DISTINCT A.USERCODE,D.OPTID, D.OPTNAME , D.PREOPTID  ,
            D.FORMCODE  , D.OPTURL, D.OPTROUTE, D.MSGNO , D.MSGPRM, D.ISINTOOLBAR ,
            D.IMGINDEX,D.TOPOPTID ,D.ORDERIND,D.PAGETYPE,D.OPTTYPE
FROM F_V_USERROLES A  JOIN F_ROLEPOWER B ON (A.ROLECODE=B.ROLECODE)
         JOIN F_OPTDEF  C ON(B.OPTCODE=C.OPTCODE)
        JOIN F_OPTINFO D ON(C.OPTID=D.OPTID)
WHERE D.OPTURL<>'...';

/*==============================================================*/
/* VIEW: F_V_OPTDEF_URL_MAP                                     */
/*==============================================================*/
CREATE OR REPLACE VIEW F_V_OPTDEF_URL_MAP AS
 SELECT CONCAT(C.OPTURL, B.OPTURL) AS OPTDEFURL, B.OPTREQ, B.OPTCODE,
    B.OPTDESC,B.OPTMETHOD , C.OPTID,B.OPTNAME
 FROM F_OPTDEF B JOIN F_OPTINFO C
    ON (B.OPTID = C.OPTID)
 WHERE C.OPTTYPE <> 'W'
   AND C.OPTURL <> '...' AND B.OPTREQ IS NOT NULL;

/*==============================================================*/
/* VIEW: V_OPT_TREE                                             */
/*==============================================================*/
CREATE OR REPLACE VIEW V_OPT_TREE AS
   SELECT I.OPTID AS MENU_ID,I.PREOPTID AS PARENT_ID,I.OPTNAME AS MENU_NAME,I.ORDERIND
   FROM F_OPTINFO I WHERE I.ISINTOOLBAR ='Y'
   UNION ALL
   SELECT D.OPTCODE AS MENU_ID,D.OPTID AS PARENT_ID,D.OPTNAME AS MENU_NAME,0 AS ORDERIND
   FROM F_OPTDEF D
;
