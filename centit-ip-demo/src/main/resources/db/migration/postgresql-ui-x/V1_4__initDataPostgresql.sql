delete from f_optinfo;
delete from f_optdef
delete from F_ROLEPOWER where role_code='sysadmin';

insert into F_UNITROLE (UNIT_CODE, ROLE_CODE, OBTAIN_DATE, SECEDE_DATE, CHANGE_DESC, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('U00001', 'deploy', '2014-12-12', null, '', null, now(), '', '');
insert into F_UNITROLE (UNIT_CODE, ROLE_CODE, OBTAIN_DATE, SECEDE_DATE, CHANGE_DESC, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('U00001', 'sysadmin', '2014-12-12', null, '', null, now(), '', '');
-- 初始化业务菜单
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('PLATFORM', '集成平台', '0', '...', '...', '', 'O', null, '', 'Y', null, '', null, '', 'D', 'icon-ok', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('DEPTMAG', '部门管理', 'PLATFORM', '...', '...', '', 'O', null, '', 'Y', null, '', null, '', 'I', 'ios-people', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('DEPTROLE', '部门角色定义', 'DEPTMAG', '/department/role', '/system/roleinfo', '', 'O', null, '', 'N', null, '', 0, '', 'D', 'icon-base icon-base-gear', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('DEPTUSERINFO', '部门用户管理', 'DEPTMAG', '/department/user', '/system/unitinfo', '', 'O', null, '', 'N', null, '', null, '', 'D', '', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('DICTSET_M', '数据字典', 'SYS_CONFIG', '/dictionary', '/system/dictionary', '', 'O', null, '', 'Y', null, '', null, '', 'D', 'logo-angular', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('LOGINCAS', 'CAS登录入口', 'PLATFORM', '/system/mainframe/logincas', '/system/mainframe', '', 'O', null, '', 'N', null, '', null, '', 'D', '', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('OPT_LOG_QUERY', '系统日志', 'SYS_CONFIG', '/log', '/system/optlog', '', 'O', null, '', 'Y', null, '', null, '', 'D', 'ios-barcode', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('SYS_CONFIG', '系统维护', 'PLATFORM', '...', '...', '', 'O', null, '', 'Y', null, '', null, '', 'D', 'ios-cog', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('UNITINFO', '机构维护', 'ORGMAG', '/unit', '/system/unitinfo', '', 'O', null, '', 'Y', null, '', null, '', 'D', 'ios-people', null, null, null, null, '', '');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('USERMAG', '用户管理', 'ORGMAG', '/user', '/system/userinfo', '', 'O', null, '', 'Y', null, '', null, '', 'D', '', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('USERROLE', '用户角色', 'ORGMAG', '/modules/sys/userrole.html', '/system/userrole', '', 'O', null, '', 'N', null, '', null, '', 'D', '', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('USERUNIT', '用户机构', 'ORGMAG', '/modules/sys/userunit.html', '/system/userunit', '', 'O', null, '', 'N', null, '', null, '', 'D', '', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('ORGMAG', '组织与权限', 'PLATFORM', '...', '...', '', 'O', null, '', 'Y', null, '', 3, '', 'I', 'ios-settings', null, null, null, null, 'u0000000', 'u0000000');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('menu', '菜单管理', 'SYS_CONFIG', '/menu', '...', '', 'O', null, '', 'Y', null, '', null, '', 'D', '', null, null, null, null, '', '');
insert into f_optinfo (OPT_ID, OPT_NAME, PRE_OPT_ID, OPT_ROUTE, OPT_URL, FORM_CODE, OPT_TYPE, MSG_NO, MSG_PRM, IS_IN_TOOLBAR, IMG_INDEX, TOP_OPT_ID, ORDER_IND, FLOW_CODE, PAGE_TYPE, ICON, HEIGHT, WIDTH, UPDATE_DATE, CREATE_DATE, CREATOR, UPDATOR) values ('ROLE', '权限管理', 'ORGMAG', '/role', '...', '', 'O', null, '', 'Y', null, '', null, '', 'D', '', null, null, null, null, '', '');


insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('2', 'DEPTMAG', '查看', 'list', '/*', '查看', null, 'F', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('3', 'DEPTPOW', '查看', 'list', '/*', '查看', null, 'F', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('7', 'ORGMAG', '查看', 'list', '/*', '查看', null, 'F', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('10', 'SYS_CONFIG', '查看', 'list', '/*', '查看', null, 'F', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('130', 'DICTSET_M', '编辑字典明细', 'updateDetail', '/update/*', '编辑字典明细', null, '', null, null, 'U', '', '');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('146', 'DEPTUSERINFO', '列表入口', 'list', '/dummy', '查看', null, 'F', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('154', 'DEPTROLE', '查看', 'list', '/unit/*', '查看某个具体机构的角色', null, 'F', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('172', 'UNITINFO', '列表入口', '', '/dummy', '机构维护列表入口', null, '', null, null, 'R', '', '');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000029', 'USERUNIT', '新增用户机构关联', 'create', '/', '添加用户关联机构', null, '', null, null, 'C', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000030', 'USERUNIT', '编辑用户机构关联', 'update', '/*', '更新用户机构关联信息', null, '', null, null, 'U', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000031', 'USERUNIT', '删除用户机构关联', 'delete', '/*', '删除用户关联机构关联', null, '', null, null, 'D', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000032', 'USERROLE', '新增用户角色关联', '', '/', '添加用户关联角色', null, '', null, null, 'C', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000033', 'USERROLE', '编辑用户角色关联', '', '/*/*', '更新用户关联角色信息', null, '', null, null, 'U', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000034', 'USERROLE', '删除用户角色关联', '', '/*/*', '删除用户关联角色', null, '', null, null, 'D', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000041', 'OPT_LOG_QUERY', '查看日志详情', '', '/dummy', '查看日志入口', null, '', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000042', 'DICTSET_M', '查询数据字典', 'list', '/', '查询数据字典列表', null, '', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000043', 'DICTSET_M', '新增数据字典', 'create', '/', '新增数据目录', null, '', null, null, 'C', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000044', 'DICTSET_M', '编辑数据字典', 'update', '/*', '编辑数据字典', null, '', null, null, 'U', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000045', 'DICTSET_M', '删除数据字典', 'delete', '/*', '删除数据目录', null, '', null, null, 'D', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000070', 'USERMAG', '用户列表', '', '/', '用户列表', null, '', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000071', 'USERMAG', '创建用户', '', '/', '创建用户', null, '', null, null, 'C', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000072', 'USERMAG', '更新用户', '', '/*', '更新用户', null, '', null, null, 'U', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000073', 'USERMAG', '删除用户', '', '/*', '删除用户', null, '', null, null, 'D', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000080', 'LOGINCAS', 'CAS登录入口', 'logincas', '/logincas', '', null, '', null, null, 'RCU', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1000081', 'LOGINCAS', '获取菜单', 'getMenu', '/menu', '', null, '', null, null, 'R', 'u0000000', 'u0000000');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1001210', 'menu', '查看', 'search', '/changeme', '查看（系统默认）', null, '', null, null, 'CRUD', '', '');
insert into f_optdef (OPT_CODE, OPT_ID, OPT_NAME, OPT_METHOD, OPT_URL, OPT_DESC, OPT_ORDER, IS_IN_WORKFLOW, UPDATE_DATE, CREATE_DATE, OPT_REQ, CREATOR, UPDATOR) values ('1001211', 'ROLE', '查看', 'search', '/changeme', '查看（系统默认）', null, '', null, null, 'CRUD', '', '');

insert into F_ROLEPOWER(role_code,opt_code,update_Date,create_date,opt_scope_codes,CREATOR,UPDATOR)
  select 'sysadmin',opt_code,sysdate,sysdate,'',CREATOR,UPDATOR from f_optdef ;

commit;

