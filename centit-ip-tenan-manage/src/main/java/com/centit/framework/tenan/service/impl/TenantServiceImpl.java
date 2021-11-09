package com.centit.framework.tenan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IDataDictionary;
import com.centit.framework.system.dao.*;
import com.centit.framework.system.po.*;
import com.centit.product.dao.WorkGroupDao;
import com.centit.product.po.WorkGroup;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.framework.tenan.dao.*;
import com.centit.framework.tenan.po.*;
import com.centit.framework.tenan.service.TenantPowerManage;
import com.centit.framework.tenan.service.TenantService;
import com.centit.framework.tenan.vo.PageListTenantInfoQo;
import com.centit.framework.tenan.vo.TenantMemberApplyVo;
import com.centit.framework.tenan.vo.TenantMemberQo;
import com.centit.product.po.WorkGroupParameter;
import com.centit.product.service.WorkGroupManager;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.stream.Collectors;

import static com.centit.framework.common.GlobalConstValue.*;
import static com.centit.framework.tenan.constant.TenantConstant.*;
import static com.centit.framework.tenan.dao.TenantMemberApplyDao.NOT_APPROVE_ARRAY;

@Service
public class TenantServiceImpl implements TenantService {

    protected Logger logger = LoggerFactory.getLogger(TenantService.class);
    /**
     * 数据库个数上限
     */
    @Value("${databaseNumberLimit:1}")
    private int databaseNumberLimit;

    /**
     * 数据空间上限
     */
    @Value("${dataSpaceLimit:1}")
    private int dataSpaceLimit;

    /**
     * 文件服务空间上限
     */
    @Value("${fileSpaceLimit:1}")
    private int fileSpaceLimit;

    /**
     * 应用个数上限
     */
    @Value("${osNumberLimit:1}")
    private int osNumberLimit;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private StandardPasswordEncoderImpl centitPasswordEncoder;

    @Autowired
    private TenantInfoDao tenantInfoDao;

    @Autowired
    private UnitInfoDao unitInfoDao;

    @Autowired
    private UserUnitDao userUnitDao;

    @Autowired
    private TenantMemberApplyDao tenantMemberApplyDao;

    @Autowired
    private TenantBusinessLogDao tenantBusinessLogDao;

    @Autowired
    private WorkGroupDao workGroupDao;

    @Autowired
    private WorkGroupManager workGroupManager;

    @Autowired
    private TenantMemberDao tenantMemberDao;

    @Autowired
    private OsInfoDao osInfoDao;

    @Autowired
    private TenantPowerManage tenantPowerManage;


    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private DataCatalogDao dataCatalogDao;

    @Autowired
    private DataDictionaryDao dataDictionaryDao;

    @Override
    @Transactional
    public ResponseData registerUserAccount(UserInfo userInfo)  {

        String userPwd = userInfo.getUserPwd();
        String regCellPhone = userInfo.getRegCellPhone();
        String loginName = userInfo.getLoginName();
        String userName = userInfo.getUserName();
        if (StringUtils.isAnyBlank(userPwd, regCellPhone, loginName,userName)) {
            return ResponseData.makeErrorMessage("登录名,密码,手机号，用户名不能为空!");
        }

        if (checkUserAccountHasExist(userInfo)) {
            return ResponseData.makeErrorMessage("账号信息已存在，请不要重复注册!");
        }

        userInfo.setUserPin(centitPasswordEncoder.encode(userPwd));
        userInfo.setCreateDate(nowDate());
        userInfo.setUserCode(null);
        userInfo.setIsValid("W");
        userInfo.setUserType("U");
        userInfoDao.saveNewObject(userInfo);
        return ResponseData.makeSuccessResponse("注册成功!");
    }


    @Override
    @Transactional
    public ResponseData applyAddTenant(TenantInfo tenantInfo) {
        String userCode = WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
        if (StringUtils.isBlank(userCode)) {
            return ResponseData.makeErrorMessage("用户未登录,请重新登录!");
        }
        tenantInfo.setCreator(userCode);
        tenantInfo.setOwnUser(userCode);
        saveTenantInfo(tenantInfo);
        initTenant(new TenantInfo(),tenantInfo);
        return ResponseData.makeSuccessResponse("租户申请成功!");
    }


    @Override
    public ResponseData applyJoinTenant(TenantMemberApply tenantMemberApply) {

        UserInfo userInfo = userInfoDao.getUserByCode(tenantMemberApply.getUserCode());
        if (null == userInfo) {
            return ResponseData.makeErrorMessage("用户信息不存在!");
        }

        TenantInfo tenantInfo = tenantInfoDao.getObjectById(tenantMemberApply.getTopUnit());
        if (null == tenantInfo || !"T".equals(tenantInfo.getIsAvailable())) {
            return ResponseData.makeErrorMessage("租户信息不存在,或租户状态不可用!");
        }

        tenantMemberApply.setApplyTime(nowDate());
        //如果申请类型为用户申请加入租户，则unitCode为空
        if ("1".equals(tenantMemberApply.getApplyType())) {
            tenantMemberApply.setUnitCode(null);
        }
        tenantMemberApplyDao.saveTenantMemberApply(tenantMemberApply);
        return ResponseData.makeSuccessResponse("申请成功,等待对方同意!");
    }

    @Override
    public PageQueryResult listApplyInfo(Map<String, Object> parameters, PageDesc pageDesc) {

        if (null == parameters.get("applyState") && null == parameters.get("applyState_in")){
            throw new ObjectException("缺少参数applyState;");
        }
        if (null == parameters.get("userCode") && null== parameters.get("topUnit")){
            throw new ObjectException("缺少参数userCode或topUnit;");
        }
        if (null != parameters.get("applyState_in")){
            parameters.put("applyState_in",MapUtils.getString(parameters, "applyState_in").split(","));
        }
        List<TenantMemberApply> tenantMemberApplies = tenantMemberApplyDao.listObjectsByProperties(parameters);
        if (CollectionUtils.sizeIsEmpty(tenantMemberApplies)) {
            return PageQueryResult.createResult(Collections.emptyList(), pageDesc);
        }

        return PageQueryResult.createResult(formatMemberApply(tenantMemberApplies), pageDesc);
    }


    @Override
    @Transactional
    public ResponseData adminCheckTenant(TenantInfo tenantInfo) {

        if (!tenantPowerManage.userIsSystemMember()) {
            return ResponseData.makeResponseData("您不是系统用户，无法审核");
        }

        if (StringUtils.isBlank(tenantInfo.getTopUnit())) {
            return ResponseData.makeErrorMessage("topUnit字段属性为空");
        }

        if (!equalsAny(tenantInfo.getIsAvailable(), "T", "F")) {
            return ResponseData.makeErrorMessage("isAvailable字段属性有误");
        }
        TenantInfo oldTenantInfo = tenantInfoDao.getObjectById(tenantInfo.getTopUnit());
        if (null == oldTenantInfo) {
            return ResponseData.makeErrorMessage("租户不存在!");
        }
        oldTenantInfo.setMemo(tenantInfo.getMemo());
        oldTenantInfo.setIsAvailable(tenantInfo.getIsAvailable());
        oldTenantInfo.setUpdateTime(nowDate());
        tenantInfo.setUpdator(WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
        if ("F".equals(tenantInfo.getIsAvailable())) {
            oldTenantInfo.setPassTime(null);
            tenantInfoDao.updateObject(oldTenantInfo);
        }

        if ("T".equals(tenantInfo.getIsAvailable())) {
            initTenant(tenantInfo,oldTenantInfo);
        }
        return ResponseData.makeSuccessResponse();
    }


    @Override
    @Transactional
    public ResponseData agreeJoin(TenantMemberApplyVo tenantMemberApplyVo) {
        String applyState = tenantMemberApplyVo.getApplyState();
        if (!equalsAny(applyState, "3", "4")) {
            return ResponseData.makeErrorMessage("applyState属性值有误");
        }

        TenantMemberApply tenantMemberApply = new TenantMemberApply();
        BeanUtils.copyProperties(tenantMemberApplyVo, tenantMemberApply);
        tenantMemberApplyDao.updateObject(tenantMemberApply);

        //同意申请，给用户分配机构
        if (tenantMemberApply.getApplyState().equals("3")) {
            saveTenantUserUnit(tenantMemberApply);
        }
        return ResponseData.makeSuccessResponse();
    }


    @Override
    @Transactional
    public ResponseData updateUserInfo(UserInfo userinfo) {
        if (StringUtils.isBlank(userinfo.getUserCode())) {
            return ResponseData.makeErrorMessage("userCode不能为空");
        }
        if (!userinfo.getUserCode().equals(WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()))) {
            return ResponseData.makeErrorMessage("无权限修改其他人的用户信息");
        }
        UserInfo oldUserByCode = userInfoDao.getUserByCode(userinfo.getUserCode());
        if (null == oldUserByCode) {
            return ResponseData.makeErrorMessage("用户信息不存在");
        }
        userinfo.setRegEmail(null);
        userinfo.setRegCellPhone(null);
        userinfo.setUpdateDate(nowDate());
        userinfo.setUpdator(userinfo.getUserCode());
        userinfo.setCreateDate(oldUserByCode.getCreateDate());
        if (StringUtils.isNotBlank(userinfo.getUserPwd())) {
            userinfo.setUserPin(centitPasswordEncoder.encode(userinfo.getUserPwd()));
        }
        userInfoDao.updateUser(userinfo);
        //刷新缓存中的人员信息
        SecurityContextHolder.getContext()
            .setAuthentication(platformEnvironment.loadUserDetailsByUserCode(userinfo.getUserCode()));
        //人员新增更新成功后刷新缓存
        CodeRepositoryCache.evictCache("UserInfo");
        return ResponseData.makeSuccessResponse();
    }

    @Override
    @Transactional
    public ResponseData quitTenant(String topUnit, String userCode) {
        if (tenantInfoDao.userIsOwner(topUnit, userCode)) {
            return ResponseData.makeErrorMessage("租户所有者不允许退出租户");
        }
        removeTenantMemberRelation(topUnit, userCode);
        return ResponseData.makeSuccessResponse("已退出该机构!");
    }

    @Override
    public ResponseData removeTenantMember(String topUnit, String userCode) {
        if (!isTenantManger(topUnit)) {
            return ResponseData.makeErrorMessage("该用户没有操作权限!");
        }
        if (!tenantPowerManage.userIsTenantMember(userCode, topUnit)) {
            return ResponseData.makeErrorMessage("该用户不在租户内!");
        }
        if (isTenantManger(userCode, topUnit)) {
            return ResponseData.makeErrorMessage("管理员或租户所有者不允许被移除租户!");
        }
        removeTenantMemberRelation(topUnit, userCode);
        return ResponseData.makeSuccessResponse("移除成功!");
    }

    /**
     * 判断当前用户是否为管理员或者租户所有者
     *
     * @param topUnit 租户id
     * @return true：是 false ：否
     */
    private boolean isTenantManger(String topUnit) {
        return tenantPowerManage.userIsTenantAdmin(topUnit) || tenantPowerManage.userIsTenantOwner(topUnit);
    }

    /**
     * 判断指定用户是否为管理员或者租户所有者
     *
     * @param userCode 用户code
     * @param topUnit  租户id
     * @return true：是 false ：否
     */
    private boolean isTenantManger(String userCode, String topUnit) {
        return tenantPowerManage.userIsTenantAdmin(userCode, topUnit) || tenantPowerManage.userIsTenantOwner(userCode, topUnit);
    }

    /**
     * 删除用户与机构之间的关系
     *
     * @param topUnit  租户id
     * @param userCode 用户code
     */
    private void removeUserUnit(String topUnit, String userCode) {
        HashMap<String, Object> paraMap = new HashMap<>();
        paraMap.put("topUnit", topUnit);
        paraMap.put("userCode", userCode);
        userUnitDao.deleteObjectsByProperties(paraMap);
        if (!userInTenant(userCode)) {
            UserInfo userInfo = new UserInfo();
            userInfo.setIsValid("W");
            userInfo.setUserCode(userCode);
            userInfoDao.updateObject(userInfo);
        }
    }

    /**
     * 根据groupId和userCode删除数据
     *
     * @param groupId  组id
     * @param userCode 用户code
     */
    private void removeWorkGroup(String groupId, String userCode) {
        HashMap<String, Object> paraMap = new HashMap<>();
        paraMap.put("groupId", groupId);
        paraMap.put("userCode", userCode);
        workGroupDao.deleteObjectsByProperties(paraMap);
    }

    /**
     * 删除租户成员与租户之间的管理关系
     *
     * @param unitCode 租户id
     * @param userCode 用户id
     */
    private void removeTenantMemberRelation(String unitCode, String userCode) {
        removeUserUnit(unitCode, userCode);
        removeWorkGroup(unitCode, userCode);
    }

    @Override
    public ResponseData businessTenant(TenantBusinessLog tenantBusinessLog) {

        if (!tenantPowerManage.userIsTenantOwner(tenantBusinessLog.getTopUnit())) {
            //如果租户所有人和转让人不是同一个人停止交易
            return ResponseData.makeSuccessResponse("无权转让该租户!");
        }

        tenantBusinessLog.setAssignorUserCode(WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
        String assignorUserCode = tenantBusinessLog.getAssignorUserCode();
        String assigneeUserCode = tenantBusinessLog.getAssigneeUserCode();
        List<String> userCodes = CollectionsOpt.createList(assignorUserCode, assigneeUserCode);
        List<UserInfo> userInfos = getUserInfosByUserCodes(userCodes);
        UserInfo assigneeUserInfo = getUserInfoByUserCode(userInfos, assigneeUserCode);
        if (null == assigneeUserInfo) {
            return ResponseData.makeSuccessResponse("受让人信息有误!");
        }
        tenantBusinessLog.setAssigneeUserName(assigneeUserInfo.getUserName());
        UserInfo user = getUserInfoByUserCode(userInfos, assignorUserCode);
        if (null != user) {
            tenantBusinessLog.setAssignorUserName(user.getUserName());
        }
        tenantBusinessLog.setApplyBusinessTime(nowDate());
        tenantBusinessLog.setSuccessBusinessTime(nowDate());
        tenantBusinessLog.setBusinessState("T");
        tenantBusinessLogDao.saveNewObject(tenantBusinessLog);
        return ResponseData.makeSuccessResponse("转让申请提交成功!");
    }

    @Override
    public ResponseData adminCheckTenantBusiness(TenantBusinessLog tenantBusinessLog) {

        if (StringUtils.isBlank(tenantBusinessLog.getBusinessId())) {
            return ResponseData.makeErrorMessage("businessId字段属性值不能为空!");
        }
        String businessState = tenantBusinessLog.getBusinessState();
        if (!equalsAny(businessState, "T", "F")) {
            return ResponseData.makeErrorMessage("businessState字段属性值有误");
        }
        tenantBusinessLogDao.updateObject(tenantBusinessLog);
        if ("T".equals(businessState)) {

        }
        return null;
    }

    @Override
    public PageQueryResult<TenantInfo> pageListTenantApply(PageListTenantInfoQo tenantInfo, PageDesc pageDesc) {
        if (tenantPowerManage.userIsSystemMember()) {
            //如果当前用户是平台管理员，则可以查看所有人的申请信息
            tenantInfo.setOwnUser(null);
        } else {
            tenantInfo.setOwnUser(WebOptUtils.getCurrentUserCode(
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
        }
        List<TenantInfo> tenantInfos = tenantInfoDao.listObjectsByProperties(tenantInfo, pageDesc);
        return PageQueryResult.createResult(tenantInfos, pageDesc);
    }

    @Override
    public PageQueryResult pageListTenantMember(Map<String,Object> params, PageDesc pageDesc) {
        String topUnit = MapUtils.getString(params, "topUnit");
        if (StringUtils.isBlank(topUnit)){
            throw new ObjectException("topUnit 不能为空");
        }
        JSONArray userInfoJsonArray = userInfoDao.listObjectsByUnit(params, pageDesc);
        if (userInfoJsonArray.size()==0){
            return PageQueryResult.createJSONArrayResult(userInfoJsonArray,pageDesc);
        }

        List<Map> userInfoMap = extendUserRoleByWorkGroup(topUnit, userInfoJsonArray);
        return PageQueryResult.createResult(userInfoMap,pageDesc);
    }



    @Override
    @Transactional
    public ResponseData assignTenantRole(TenantMemberQo tenantMemberQo) {
        boolean check = StringUtils.isBlank(tenantMemberQo.getRoleCode())
            || StringUtils.isBlank(tenantMemberQo.getMemberUserCode())
            || StringUtils.isBlank(tenantMemberQo.getTopUnit());
        if (check) {
            return ResponseData.makeErrorMessage("参数roleCode,topUnit,memberUserCode不能为空!");
        }
        String topUnit = tenantMemberQo.getTopUnit();
        boolean userHasPower = tenantPowerManage.userIsTenantOwner(topUnit) || tenantPowerManage.userIsTenantAdmin(topUnit);
        if (!userHasPower) {
            return ResponseData.makeErrorMessage("当前人员没有操作权限!");
        }
        if (!equalsAny(tenantMemberQo.getRoleCode(), TENANT_ADMIN_ROLE_CODE, TENANT_NORMAL_MEMBER_ROLE_CODE)) {
            return ResponseData.makeErrorMessage("角色代码有误!");
        }
        updateWorkGroupRole(tenantMemberQo, topUnit);
        return ResponseData.makeSuccessResponse("操作成功!");
    }


    @Override
    public ResponseData removeApplicationMember(String groupId, String userCode) {
        if (!tenantPowerManage.userIsApplicationAdmin(groupId)) {
            return ResponseData.makeErrorMessage("当前用户没有操作权限!");
        }
        if (!tenantPowerManage.userIsApplicationMember(userCode, groupId)) {
            return ResponseData.makeErrorMessage("用户不在开发组中!");
        }
        if (tenantPowerManage.userIsApplicationAdmin(userCode, groupId)) {
            return ResponseData.makeErrorMessage("组长不允许被移除");
        }
        List<WorkGroup> workGroups = listWorkGroupByUserCodeAndTopUnit(userCode, groupId);
        if (!CollectionUtils.isEmpty(workGroups) && workGroups.size() == 1) {
            workGroupDao.deleteObjectForceById(workGroups.get(0));
        } else {
            logger.warn("移除班组人员时，发现班组人员异常,groupId={},userCode={},workGroups={}", groupId, userCode, workGroups.toString());
            return ResponseData.makeErrorMessage("发现数据异常，停止操作!");
        }
        return ResponseData.makeSuccessResponse("移除成功!");
    }

    @Override
    public ResponseData listApplicationMember(String groupId) {
        List<WorkGroup> workGroups = workGroupDao.listObjectsByProperties(CollectionsOpt.createHashMap("groupId", groupId));
        if (CollectionUtils.isEmpty(workGroups)) {
            return ResponseData.makeResponseData(CollectionUtils.emptyCollection());
        }
        List<Map> resultMaps = JSONArray.parseArray(JSONArray.toJSONString(workGroups), Map.class);
        resultMaps.forEach(map -> {
            UserInfo userInfo = userInfoDao.getUserByCode(MapUtils.getString(map, "userCode"));
            if (null != userInfo) {
                map.put("userName", userInfo.getUserName());
            } else {
                map.put("userName", "");
            }
        });
        return ResponseData.makeResponseData(resultMaps);
    }

    /**
     * 校验WorkGroup参数
     *
     * @param workGroup 工作组
     * @return 判断结果字符串
     */
    private String checkWorkGroup(WorkGroup workGroup) {
        WorkGroupParameter workGroupParameter = workGroup.getWorkGroupParameter();
        String groupId = workGroupParameter.getGroupId();
        String userCode = workGroupParameter.getUserCode();
        if (StringUtils.isAnyBlank(groupId, userCode)) {
            return "groupId,userCode不能为为空";
        }
        String roleCode = workGroupParameter.getRoleCode();
        if (!equalsAny(roleCode, APPLICATION_ADMIN_ROLE_CODE, APPLICATION_NORMAL_MEMBER_ROLE_CODE)) {
            return String.format("roleCode应该是%s,%s中的一个", APPLICATION_ADMIN_ROLE_CODE, APPLICATION_NORMAL_MEMBER_ROLE_CODE);
        }

        if (!tenantPowerManage.userIsApplicationAdmin(groupId)) {
            return "当前操作人不具备操作权限";
        }

        if (null == osInfoDao.getObjectById(workGroupParameter.getGroupId())) {
            return "groupId不存在!";
        }
        workGroup.setCreator(WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
        workGroup.setIsValid("T");
        return null;
    }


    @Override
    public ResponseData userTenants(String userCode) {
        if (StringUtils.isBlank(userCode)) {
            return ResponseData.makeErrorMessage("userCode 不能为空");
        }
        List<TenantInfo> tenantInfos = userTenantsByUserCode(userCode);
        if (CollectionUtils.sizeIsEmpty(tenantInfos)) {
            return ResponseData.makeResponseData(Collections.emptyList());
        }
        return ResponseData.makeResponseData(formatTenants(userCode, tenantInfos));
    }


    @Override
    public ResponseData listTenantApplication(String topUnit) {
        return ResponseData.makeResponseData(osInfoDao.listOsInfoByUnit(topUnit));
    }

    @Override
    public ResponseData listWorkGroupByProperties(Map<String, Object> paramMap) {
        return ResponseData.makeResponseData(workGroupDao.listObjectsByProperties(paramMap));
    }


    @Override
    public PageQueryResult<TenantInfo> pageListTenants(String unitName, PageDesc pageDesc) {
        HashMap<String, Object> filterMap = new HashMap<>();
        filterMap.put("unitName_lk", StringUtils.join("%", unitName, "%"));
        filterMap.put("isAvailable", "T");
        List<TenantInfo> tenantInfos = tenantInfoDao.listObjectsByProperties(filterMap, pageDesc);
        if (null == tenantInfos || tenantInfos.isEmpty()) {
            return PageQueryResult.createResult(tenantInfos, pageDesc);
        }
        //去除敏感信息
        tenantInfos.forEach(tenantInfo -> {
            tenantInfo.setOwnUser(null);
            tenantInfo.setSourceUrl(null);
            tenantInfo.setMemo(null);
            tenantInfo.setUseLimittime(null);
        });
        return PageQueryResult.createResult(tenantInfos, pageDesc);
    }

    @Override
    public ResponseData findUsers(Map<String, Object> paramMap) {

        String unitCode = MapUtils.getString(paramMap, "unitCode");
        String userName = MapUtils.getString(paramMap, "userName");
        String regCellPhone = MapUtils.getString(paramMap, "regCellPhone");
        String userCode = MapUtils.getString(paramMap, "userCode");
        if (StringUtils.isAllBlank(userName, regCellPhone, userCode)) {
            return ResponseData.makeErrorMessage("userName,regCellPhone,userCode不能全为空");
        }
        if (StringUtils.isBlank(unitCode)) {
            return ResponseData.makeErrorMessage("unitCode不能为空");
        }
        List<UserInfo> userInfos = userInfoDao.listObjectsByProperties(paramMap);
        if (CollectionUtils.sizeIsEmpty(userInfos)) {
            return ResponseData.makeResponseData(CollectionUtils.emptyCollection());
        }
        //排除已经被邀请的用户且没有蛇和,或者属于本单位的人员
        Set<String> userInfoUserCodes = userInfos.stream().map(UserInfo::getUserCode).collect(Collectors.toSet());
        List<TenantMemberApply> alreadyApply = tenantMemberApplyDao.listObjectsByProperties(
            CollectionsOpt.createHashMap("userCode_in", CollectionsOpt.listToArray(userInfoUserCodes),
                "topUnit", unitCode,"applyState_in",NOT_APPROVE_ARRAY ));
        List<String> applyUserCodes = alreadyApply.stream().map(TenantMemberApply::getUserCode).collect(Collectors.toList());
        List<UserUnit> userUnits = userUnitDao.listObjects(CollectionsOpt.createHashMap("topUnit", unitCode));
        userInfos = userInfos.stream().filter(userInfo ->
            null == getUserUnitByUserCode(userUnits, userInfo.getUserCode())&& !applyUserCodes.contains(userInfo.getUserCode())).collect(Collectors.toList());
        return ResponseData.makeResponseData(userInfos);
    }

    @Override
    public ResponseData cancelApply(Map<String, Object> parameters) {
        TenantMemberApply tenantMemberApply = tenantMemberApplyDao.getObjectByProperties(parameters);
        if (null !=tenantMemberApply && StringUtils.equalsAny(tenantMemberApply.getApplyState(),"1","2")){
            tenantMemberApplyDao.deleteObject(tenantMemberApply);
        }
        return ResponseData.makeSuccessResponse();
    }

    @Override
    public ResponseData deleteTenant(Map<String, Object> parameters) {

        if(!tenantPowerManage.userIsTenantOwner(MapUtils.getString(parameters,"topUnit"))){
            return ResponseData.makeErrorMessage("只有租户所有者才能注销租户");
        }
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setTopUnit(MapUtils.getString(parameters,"topUnit"));
        tenantInfo.setIsAvailable("F");
        tenantInfoDao.updateObject(tenantInfo);
        return ResponseData.makeSuccessResponse();

    }


    /**
     * 通过userCode userName regCellPhone精确查找用户
     *
     * @param paramMap
     * @return
     */
    private List<UserInfo> getUserInfosByExactUserName(Map<String, Object> paramMap) {
        Map<String, Object> filterMap = filterMapAbsentKey(new String[]{"userCode", "userName", "regCellPhone"}, paramMap);
        String querySql = "select  USER_CODE, USER_PIN, IS_VALID, USER_TYPE, LOGIN_NAME, USER_NAME, ENGLISH_NAME, USER_DESC, LOGIN_TIMES, ACTIVE_TIME, PWD_EXPIRED_TIME, REG_EMAIL, USER_PWD, REG_CELL_PHONE, ID_CARD_NO, USER_WORD, USER_TAG, USER_ORDER, TOP_UNIT, PRIMARY_UNIT, CREATE_DATE, CREATOR, UPDATOR, UPDATE_DATE from F_USERINFO where 1=1  [  userName| and USER_NAME = :userName ] [ userCode| AND USER_CODE = :userCode ]  [ regCellPhone | AND REG_CELL_PHONE = :regCellPhone ]";
        QueryAndNamedParams qapSql = QueryUtils.translateQuery(querySql, filterMap);
        return userInfoDao.listObjectsBySql(qapSql.getQuery(), paramMap);
    }


    /**
     * 筛选出map中指定key对应的数据
     *
     * @param keys 需要筛选出的key
     * @param map  需要呗筛选的map
     * @return 筛选后的结果 如果map中不存在keys中指定的key，结果中也不会存在
     */
    private Map<String, Object> filterMapAbsentKey(String[] keys, Map<String, Object> map) {
        if (CollectionUtils.sizeIsEmpty(map)) {
            return new HashMap<>();
        }
        ArrayList<String> keyList = new ArrayList<>(map.keySet());
        HashMap<String, Object> resultMap = new HashMap<>();
        for (String key : keys) {
            if (keyList.contains(key)) {
                resultMap.put(key, map.get(key));
            }
        }
        return resultMap;
    }

    /**
     * 通过osInfo 信息保存workGroup
     *
     * @param osInfo 应用基本信息
     */
    private void saveWorkGroupByOSInfo(OsInfo osInfo) {
        osInfoDao.saveNewObject(osInfo);
        WorkGroup workGroup = new WorkGroup();
        WorkGroupParameter workGroupParameter = workGroup.getWorkGroupParameter();
        workGroupParameter.setGroupId(osInfo.getOsId());
        workGroupParameter.setUserCode(osInfo.getCreated());
        workGroupParameter.setRoleCode(APPLICATION_ADMIN_ROLE_CODE);
        workGroup.setCreator(osInfo.getCreated());
        workGroup.setIsValid("T");
        workGroupDao.saveNewObject(workGroup);
    }


    /**
     * 在TenantInfo中添加字段isOwner
     *
     * @param userCode
     * @param tenantInfos
     * @return
     */
    private List<Map> formatTenants(String userCode, List<TenantInfo> tenantInfos) {
        Set<String> topUnitCodes = new HashSet<>();
        if (CollectionUtils.isNotEmpty(tenantInfos)) {
            topUnitCodes = tenantInfos.stream().map(TenantInfo::getTopUnit).collect(Collectors.toSet());
        }
        List<WorkGroup> workGroups = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(topUnitCodes)) {
            workGroups = listWorkGroupByUserCodeAndTopUnit(userCode, CollectionsOpt.listToArray(topUnitCodes));
        }
        UserInfo userInfo = userInfoDao.getUserByCode(userCode);
        String primaryUnit = "";
        if (null != userInfo) {
            primaryUnit = userInfo.getPrimaryUnit();
        }
        List<Map> tenantJsonArrays = JSONArray.parseArray(JSONArray.toJSONString(tenantInfos), Map.class);
        if (CollectionUtils.isNotEmpty(tenantJsonArrays)) {
            for (Map tenantJson : tenantJsonArrays) {
                extendTenantsAttribute(userCode, workGroups, primaryUnit, tenantJson);
            }
        }
        return tenantJsonArrays;
    }

    /**
     * 补充Tenant中的字段信息
     *
     * @param userCode    用户code
     * @param workGroups  成员角色
     * @param primaryUnit 用户主机构
     * @param tenantJson  Tenant
     */
    @SuppressWarnings("unchecked")
    private void extendTenantsAttribute(String userCode, List<WorkGroup> workGroups, String primaryUnit, Map tenantJson) {
        if (MapUtils.getString(tenantJson, "ownUser").equals(userCode)) {
            tenantJson.put("isOwner", "T");
        } else {
            tenantJson.put("isOwner", "F");
        }

        String topUnit = MapUtils.getString(tenantJson, "topUnit");
        WorkGroup workGroup = getWorkGroupByUserCodeAndTopUnit(userCode, topUnit, workGroups);
        String roleCode = null;
        if (null != workGroup) {
            roleCode = workGroup.getWorkGroupParameter().getRoleCode();
            tenantJson.put("roleCode", roleCode);
        } else {
            tenantJson.put("roleCode", "");
        }
        tenantJson.put("roleName", translateTenantRole(roleCode));

        //判断当前用户是否为租户所有者或管理员
        boolean isAdmin = MapUtils.getString(tenantJson, "roleCode", "").equals(TENANT_ADMIN_ROLE_CODE)
            || MapUtils.getString(tenantJson, "isOwner").equals("T");
        tenantJson.put("isAdmin", isAdmin);

        if (MapUtils.getString(tenantJson, "topUnit").equals(primaryUnit)) {
            tenantJson.put("isPrimaryUnit", "T");
        } else {
            tenantJson.put("isPrimaryUnit", "F");
        }
        List<UserUnit> userUnits = userUnitDao.listObjectsByProperties(CollectionsOpt.createHashMap("userCode", userCode, "topUnit", topUnit));
        extendTenantsUserRanks(tenantJson, userUnits);

    }

    /**
     * 翻译租户角色名称
     *
     * @param roleCode 租户角色code
     * @return 租户角色名称
     */
    private String translateTenantRole(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            return "";
        }
        switch (roleCode) {
            case TENANT_ADMIN_ROLE_CODE:
                return "管理员";
            case TENANT_NORMAL_MEMBER_ROLE_CODE:
                return "组员";
            default:
                return "";
        }

    }

    /**
     * 补充Tenant中UserRank字段及其翻译
     *
     * @param tenantJson Tenant集合
     * @param userUnits  userUnits集合
     */
    private void extendTenantsUserRanks(Map tenantJson, List<UserUnit> userUnits) {
        if (CollectionUtils.sizeIsEmpty(userUnits)) {
            tenantJson.put("userRank", new ArrayList<>());
            tenantJson.put("userRankText", new ArrayList<>());
            tenantJson.put("deptCodes", new ArrayList<>());
            tenantJson.put("deptNames", new ArrayList<>());
            return;
        }
        List<String> userRanks = userUnits.stream().filter(userUnit -> StringUtils.isNotBlank(userUnit.getUserRank()))
            .map(UserUnit::getUserRank).collect(Collectors.toList());
        tenantJson.put("userRank", Optional.ofNullable(userRanks).orElse(new ArrayList<>()));
        ArrayList<String> userRankTexts = new ArrayList<>();
        for (String userRank : userRanks) {
            IDataDictionary rankType = CodeRepositoryUtil.getDataPiece("RankType", userRank);
            if (null != rankType) {
                userRankTexts.add(rankType.getDataValue());
            }
        }
        tenantJson.put("userRankText", userRankTexts);
        Set<String> unitCodes = userUnits.stream().map(UserUnit::getTopUnit).collect(Collectors.toSet());
        List<UnitInfo> unitInfos = unitInfoDao.listObjectsByProperties(
            CollectionsOpt.createHashMap("unitCode_in", CollectionsOpt.listToArray(unitCodes)));
        //部门信息
        tenantJson.put("deptCodes", unitCodes);
        tenantJson.put("deptNames", unitInfos.stream().map(UnitInfo::getUnitName).collect(Collectors.toSet()));

        tenantJson.put("deptList", transformDeptList(userUnits, unitInfos));
    }

    /**
     * 整合单位结构数据
     * @param userUnits 用户单位关联信息
     * @param unitInfos 机构信息
     * @return
     */
    private ArrayList<HashMap<String, Object>> transformDeptList(List<UserUnit> userUnits, List<UnitInfo> unitInfos) {
        Map<String, List<UserUnit>> groupByUnitCode = userUnits.stream().collect(Collectors.groupingBy(UserUnit::getUnitCode));
        ArrayList<HashMap<String, Object>> deptList = new ArrayList<>();
        for (String key : groupByUnitCode.keySet()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("deptCode",key);
            String unitName = unitInfos.stream().filter(unitInfo -> unitInfo.getUnitCode().equals(key)).findAny().map(UnitInfo::getUnitName).orElseGet(() -> "");
            map.put("deptName",unitName);
            //获取岗位集合
            List<HashMap<String, Object>> userRankList = groupByUnitCode.get(key).stream().map(userUnit -> {
                HashMap<String, Object> map1 = new HashMap<>();
                map1.put("userRank", userUnit.getUserRank());
                IDataDictionary rankType = CodeRepositoryUtil.getDataPiece("RankType", userUnit.getUserRank());
                if (null != rankType){
                    map1.put("userRankText",rankType.getDataValue());
                }else {
                    map1.put("userRankText","");
                }
                return map1;
            }).collect(Collectors.toList());
            map.put("userRankList",userRankList);
            deptList.add(map);
        }
        return deptList;
    }

    /**
     * 通过userCode 和topUnit 获取WorkGroup
     *
     * @param userCode
     * @param topUnit
     * @param workGroups
     * @return
     */
    private WorkGroup getWorkGroupByUserCodeAndTopUnit(String userCode, String topUnit, List<WorkGroup> workGroups) {
        if (CollectionUtils.sizeIsEmpty(workGroups)) {
            return null;
        }
        for (WorkGroup workGroup : workGroups) {
            WorkGroupParameter workGroupParameter = workGroup.getWorkGroupParameter();
            if (userCode.equals(workGroupParameter.getUserCode()) && topUnit.equals(workGroupParameter.getGroupId())) {
                return workGroup;
            }
        }
        return null;
    }

    /**
     * 更新角色
     *
     * @param tenantMemberQo 租户成员查询实体类
     * @param topUnit        租户id
     */
    private void updateWorkGroupRole(TenantMemberQo tenantMemberQo, String topUnit) {
        WorkGroup workGroup = new WorkGroup();
        WorkGroupParameter workGroupParameter = workGroup.getWorkGroupParameter();
        workGroupParameter.setGroupId(topUnit);
        workGroupParameter.setUserCode(tenantMemberQo.getMemberUserCode());
        workGroupParameter.setRoleCode(tenantMemberQo.getRoleCode());
        workGroup.setUpdateDate(nowDate());
        workGroup.setUpdator(WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
        updateWorkGroupRole(workGroup);
    }

    /**
     * 更新角色
     *
     * @param workGroup 工作组
     */
    private void updateWorkGroupRole(WorkGroup workGroup) {
        Map<String, Object> hashMap = CollectionsOpt.createHashMap("groupId",
            workGroup.getWorkGroupParameter().getGroupId(),
            "userCode", workGroup.getWorkGroupParameter().getUserCode());
        List<WorkGroup> workGroups = workGroupDao.listObjectsByProperties(hashMap);
        if (workGroups.size() > 1) {
            throw new ObjectException("检测到该人员在一个租户中的角色个数大于一个，操作停止!");
        }
        if (workGroups.size() == 1) {
            WorkGroupParameter workGroupParameter = workGroups.get(0).getWorkGroupParameter();
            workGroup.getWorkGroupParameter().setRoleCode(workGroupParameter.getRoleCode());
            workGroupManager.updateWorkGroup(workGroup);
        }
        if (workGroups.size() == 0) {
            workGroup.setCreator(WebOptUtils.getCurrentUserCode(
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
            workGroup.setAuthTime(nowDate());
            workGroupDao.saveNewObject(workGroup);
        }
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    private Date nowDate() {
        return new Date(System.currentTimeMillis());
    }


    /**
     * 根据userInfo信息验证账户是否存在
     *
     * @param userInfo
     * @return true:用户已经存在 false：用户不存在
     */
    private boolean checkUserAccountHasExist(UserInfo userInfo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("g0_loginName", userInfo.getLoginName());
        map.put("g1_userName", userInfo.getUserName());
        if (StringUtils.isNotBlank(userInfo.getRegEmail())) {
            map.put("g2_regEmail", userInfo.getRegEmail());
        }
        if (StringUtils.isNotBlank(userInfo.getRegCellPhone())) {
            map.put("g3_regCellPhone", userInfo.getRegCellPhone());
        }
        if (StringUtils.isNotBlank(userInfo.getIdCardNo())) {
            map.put("g4_idCardNo", userInfo.getIdCardNo());
        }
        UserInfo oldUserInfo = userInfoDao.getObjectByProperties(map);

        return null != oldUserInfo;
    }

    /**
     * 根据tenantInfo和unitInfo保存userUnit（人员机构关联）信息
     *
     * @param tenantInfo
     * @param unitInfo
     */
    private void saveUserUnitByTenantAndUnit(TenantInfo tenantInfo, UnitInfo unitInfo) {
        UserUnit userUnit = new UserUnit();
        userUnit.setUserUnitId(UuidOpt.getUuidAsString32());
        userUnit.setUserCode(tenantInfo.getOwnUser());
        userUnit.setUserOrder(0L);
        userUnit.setUnitCode(unitInfo.getUnitCode());
        userUnit.setTopUnit(unitInfo.getTopUnit());
        userUnit.setCreateDate(unitInfo.getCreateDate());
        userUnit.setCreator(tenantInfo.getOwnUser());
        userUnitDao.saveNewObject(userUnit);
    }

    /**
     * 根据tenantInfo信息保存unitInfo
     *
     * @param tenantInfo
     * @return
     */
    private UnitInfo saveUnitInfoByTenantInfo(TenantInfo tenantInfo) {
        UnitInfo unitInfo = new UnitInfo();
        unitInfo.setCreateDate(tenantInfo.getCreateTime());
        unitInfo.setCreator(tenantInfo.getCreator());
        unitInfo.setIsValid("T");
        unitInfo.setTopUnit(tenantInfo.getTopUnit());
        unitInfo.setUnitCode(tenantInfo.getTopUnit());
        unitInfo.setUnitManager(tenantInfo.getCreator());
        unitInfo.setUnitName(tenantInfo.getUnitName());
        unitInfo.setUnitType("E");
        unitInfoDao.saveNewObject(unitInfo);
        return unitInfo;
    }

    /**
     * 保存tenantInfo
     *
     * @param tenantInfo
     * @return
     */
    private TenantInfo saveTenantInfo(TenantInfo tenantInfo) {
        Date currentDate = nowDate();
        tenantInfo.setPassTime(null);
        tenantInfo.setMemo(null);
        tenantInfo.setIsAvailable(null);
        tenantInfo.setApplyTime(currentDate);
        tenantInfo.setCreateTime(currentDate);
        tenantInfo.setTopUnit(null);
        tenantInfo.setDataSpaceLimit(0);
        tenantInfo.setFileSpaceLimit(0);
        tenantInfo.setDatabaseNumberLimit(0);
        tenantInfo.setOsNumberLimit(0);
        tenantInfoDao.saveNewObject(tenantInfo);
        return tenantInfo;
    }


    /**
     * 给租户下的人分配单位
     *
     * @param tenantMemberApply 申请信息
     */
    private void saveTenantUserUnit(TenantMemberApply tenantMemberApply) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserCode(tenantMemberApply.getUserCode());
        userInfo.setIsValid("T");
        userInfoDao.updateObject(userInfo);
        TenantMemberApply oldTenantMemberApply = tenantMemberApplyDao.getObjectById(tenantMemberApply);
        if (StringUtils.isBlank(oldTenantMemberApply.getUnitCode())) {
            //如果没有指定分配到哪个部门，则默认分配到顶级部门
            oldTenantMemberApply.setUnitCode(oldTenantMemberApply.getTopUnit());
        }
        List<UserUnit> userUnits = userUnitDao.listObjectByUserUnit(oldTenantMemberApply.getUserCode(), oldTenantMemberApply.getUnitCode());
        if (userUnits.size() == 0) {
            UserUnit userUnit = new UserUnit();
            userUnit.setUserCode(oldTenantMemberApply.getUserCode());
            userUnit.setUnitCode(oldTenantMemberApply.getUnitCode());
            userUnit.setTopUnit(oldTenantMemberApply.getTopUnit());
            userUnit.setCreator(oldTenantMemberApply.getInviterUserCode());
            userUnitDao.saveNewObject(userUnit);
        }
    }


    /**
     * 格式化tenantMemberApplies
     *
     * @param tenantMemberApplies
     */
    private List<TenantMemberApplyVo> formatMemberApply(List<TenantMemberApply> tenantMemberApplies) {
        HashSet<String> topUnitCodes = new HashSet<>();
        HashSet<String> userCodes = new HashSet<>();
        for (TenantMemberApply tenantMemberApply : tenantMemberApplies) {
            topUnitCodes.add(tenantMemberApply.getTopUnit());
            userCodes.add(tenantMemberApply.getUserCode());
            userCodes.add(tenantMemberApply.getInviterUserCode());
        }
        List<UnitInfo> unitInfos = getUnitInfosByUnitCodes(new ArrayList<>(topUnitCodes));
        List<UserInfo> userInfos = getUserInfosByUserCodes(new ArrayList<>(userCodes));

        ArrayList<TenantMemberApplyVo> tenantMemberApplyVos = new ArrayList<>();
        for (TenantMemberApply tenantMemberApply : tenantMemberApplies) {
            TenantMemberApplyVo tenantMemberApplyVo = new TenantMemberApplyVo();
            BeanUtils.copyProperties(tenantMemberApply, tenantMemberApplyVo);
            UnitInfo unitInfo = getUnitInfoByUnitCode(unitInfos, tenantMemberApply.getTopUnit());
            UserInfo userInfo = getUserInfoByUserCode(userInfos, tenantMemberApply.getUserCode());
            UserInfo inviterUserInfo = getUserInfoByUserCode(userInfos, tenantMemberApply.getInviterUserCode());
            if (null != unitInfo) {
                tenantMemberApplyVo.setUnitName(unitInfo.getUnitName());
            }
            if (null != userInfo) {
                tenantMemberApplyVo.setUserName(userInfo.getUserName());
            }
            if (null != inviterUserInfo) {
                tenantMemberApplyVo.setInviterUserName(inviterUserInfo.getUserName());
            }
            tenantMemberApplyVos.add(tenantMemberApplyVo);
        }

        return tenantMemberApplyVos;
    }

    /**
     * 根据topUnitCode集合批量获取单位信息
     *
     * @param topUnitCodes
     * @return
     */
    private List<UnitInfo> getUnitInfosByUnitCodes(List<String> topUnitCodes) {
        HashMap<String, Object> topUnitParamMap = new HashMap<>();
        topUnitParamMap.put("unitCode_in", CollectionsOpt.listToArray(topUnitCodes));
        return unitInfoDao.listObjectsByProperties(topUnitParamMap);
    }

    /**
     * 根据usercode集合批量获取用户信息
     *
     * @param userCodes
     * @return
     */
    private List<UserInfo> getUserInfosByUserCodes(List<String> userCodes) {
        HashMap<String, Object> userCodeParamMap = new HashMap<>();
        userCodeParamMap.put("userCode_in", CollectionsOpt.listToArray(userCodes));
        return userInfoDao.listObjectsByProperties(userCodeParamMap);
    }

    /**
     * 根据unitCode获取unit详情
     *
     * @param unitInfos
     * @param topUnit
     * @return
     */
    private UnitInfo getUnitInfoByUnitCode(List<UnitInfo> unitInfos, String topUnit) {
        for (UnitInfo unitInfo : unitInfos) {
            if (topUnit.equals(unitInfo.getUnitCode())) {
                return unitInfo;
            }
        }
        return null;
    }

    /**
     * 根据userCode获取UserUnit详情
     *
     * @param userUnits UserUnit集合
     * @param userCode  用户code
     * @return 第一个符合条件的UserUnit
     */
    private UserUnit getUserUnitByUserCode(List<UserUnit> userUnits, String userCode) {
        for (UserUnit userUnit : userUnits) {
            if (userCode.equals(userUnit.getUserCode())) {
                return userUnit;
            }
        }
        return null;
    }

    /**
     * 根据userCode获取user详情
     *
     * @param userInfos
     * @param userCode
     * @return
     */
    private UserInfo getUserInfoByUserCode(List<UserInfo> userInfos, String userCode) {
        for (UserInfo userInfo : userInfos) {
            if (userCode.equals(userInfo.getUserCode())) {
                return userInfo;
            }
        }
        return null;
    }



    /**
     * 判断后面的字符是否包含第一个字符
     *
     * @param cs
     * @param searchCharSequences
     * @return
     */
    private boolean equalsAny(CharSequence cs, CharSequence... searchCharSequences) {
        if (!StringUtils.isEmpty(cs) && !ArrayUtils.isEmpty(searchCharSequences)) {
            CharSequence[] var2 = searchCharSequences;
            int var3 = searchCharSequences.length;
            for (int var4 = 0; var4 < var3; ++var4) {
                CharSequence searchCharSequence = var2[var4];
                if (cs.equals(searchCharSequence)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }


    /**
     * 用户是否已经归属与租户
     *
     * @param userCode 用户code
     * @return true：归属于至少一个租户
     */
    private boolean userInTenant(String userCode) {
        List<UserUnit> userUnits = userUnitDao.listObjects(CollectionsOpt.createHashMap("userCode", userCode));
        if (CollectionUtils.sizeIsEmpty(userUnits)) {
            return false;
        }
        Set<String> topUnits = userUnits.stream().filter(userUnit -> StringUtils.isNotBlank(userUnit.getTopUnit()))
            .map(UserUnit::getTopUnit).collect(Collectors.toSet());
        List<TenantInfo> tenantInfos = tenantInfoDao.listObjects(CollectionsOpt.createHashMap("topUnit_in",
            CollectionsOpt.listToArray(topUnits)));
        if (CollectionUtils.sizeIsEmpty(tenantInfos)) {
            return false;
        }
        return true;
    }

    /**
     * 获取用户所在的租户
     *
     * @param userCode
     * @return
     */
    private List<TenantInfo> userTenantsByUserCode(String userCode) {
        List<UserUnit> userUnits = userUnitDao.listObjects(CollectionsOpt.createHashMap("userCode", userCode));
        if (CollectionUtils.sizeIsEmpty(userUnits)) {
            return null;
        }
        Set<String> topUnits = userUnits.stream().map(UserUnit::getTopUnit).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        if (CollectionUtils.sizeIsEmpty(topUnits)) {
            return null;
        }
        return tenantInfoDao.listObjectsByProperties(CollectionsOpt.createHashMap("topUnit_in",
            CollectionsOpt.listToArray(topUnits),"isAvailable","T"));

    }

    /**
     * 根据userCode和topUnit查询数据
     *
     * @param userCode
     * @param topUnits
     * @return
     */
    private List<WorkGroup> listWorkGroupByUserCodeAndTopUnit(String userCode, String... topUnits) {
        HashMap<String, Object> map = new HashMap<>();
        if (null != topUnits && topUnits.length > 0) {
            map.put("groupId_in", topUnits);
        }
        map.put("userCode", userCode);
        return workGroupDao.listObjectsByProperties(map);
    }

    /**
     * 把租户所有者设置租户角色
     *
     * @param tenantInfo
     */
    private void saveTenantOwnerToWorkGroup(TenantInfo tenantInfo) {
        WorkGroup workGroup = new WorkGroup();
        WorkGroupParameter workGroupParameter = new WorkGroupParameter();
        workGroupParameter.setRoleCode(TENANT_ADMIN_ROLE_CODE);
        workGroupParameter.setGroupId(tenantInfo.getTopUnit());
        workGroupParameter.setUserCode(tenantInfo.getOwnUser());
        workGroup.setWorkGroupParameter(workGroupParameter);
        workGroup.setCreator(WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
        workGroup.setIsValid("T");
        workGroupManager.createWorkGroup(workGroup);
    }

    /**
     * 根据租户信息创建机构信息的和租户与机构的关系
     * 根据当前用户是否为租户所有者判断是否具有资源申请，租户转让的能力
     * 设置租户所有者为租户管理员
     * 设置租户所有者为机构管理员
     * 初始化字典值
     *
     * @param oldTenantInfo
     */
    private void saveTenantRelationData(TenantInfo oldTenantInfo) {
        UnitInfo unitInfo = saveUnitInfoByTenantInfo(oldTenantInfo);
        saveUserUnitByTenantAndUnit(oldTenantInfo, unitInfo);
        saveTenantOwnerToWorkGroup(oldTenantInfo);
        saveUserRole(oldTenantInfo.getOwnUser(), SYSTEM_ADMIN_ROLE_CODE);
        initTenantDictionary(oldTenantInfo.getTopUnit(), oldTenantInfo.getOwnUser());
    }

    /**
     * 保存用户角色
     *
     * @param userCode 租户code
     * @param roleCode 角色code
     */
    private void saveUserRole(String userCode, String roleCode) {
        List<UserRole> userRoles = userRoleDao.listObjectsByProperties(
            CollectionsOpt.createHashMap("userCode", userCode, "roleCode", roleCode));
        if (!CollectionUtils.sizeIsEmpty(userRoles)) {
            return;
        }
        UserRole userRole = new UserRole();
        UserRoleId userRoleId = new UserRoleId();
        userRoleId.setRoleCode(roleCode);
        userRoleId.setUserCode(userCode);
        userRole.setId(userRoleId);
        userRole.setObtainDate(new Date(System.currentTimeMillis()));
        userRole.setCreator(WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
        userRoleDao.saveNewObject(userRole);
    }

    /**
     * 初始化租户的字典值
     *
     * @param topUnitCode 租户code
     * @param userCode    创建人用户code
     */
    private void initTenantDictionary(String topUnitCode, String userCode) {

        //用户类型数据字典userType
        DataCatalog userTypeDataCatalog = defaultDataCatalog(userCode, topUnitCode,
            DATA_CATALOG_UESR_TYPE_SUFFIX, "用户类型");
        dataCatalogDao.saveNewObject(userTypeDataCatalog);
        String[][] userTypeElements = {{userTypeDataCatalog.getCatalogCode(), "A", "普通用户"}};
        batchSaveDataDictionary(userTypeElements);

        //机构类型数据字典unitType
        DataCatalog unitTypeDataCatalog = defaultDataCatalog(userCode, topUnitCode,
            DATA_CATALOG_UNIT_TYPE_SUFFIX, "机构类别");
        dataCatalogDao.saveNewObject(unitTypeDataCatalog);
        String unitTypeCatalogCode = unitTypeDataCatalog.getCatalogCode();
        String[][] unitTypeElements = {{unitTypeCatalogCode, "A", "一般机构"}, {unitTypeCatalogCode, "I", "项目组"}, {unitTypeCatalogCode, "O", "业务机构"}};
        batchSaveDataDictionary(unitTypeElements);

        //用户职务类型字典 RankType
        DataCatalog rankTypeDataCatalog = defaultDataCatalog(userCode, topUnitCode,
            DATA_CATALOG_RANK_SUFFIX, "职位");
        dataCatalogDao.saveNewObject(rankTypeDataCatalog);
        String rankTypeCatalogCode = rankTypeDataCatalog.getCatalogCode();
        String[][] rankTypeElements = {{rankTypeCatalogCode, "CM", "董事长"}, {rankTypeCatalogCode, "DM", "部门经理"}, {rankTypeCatalogCode, "FG", "分管高层"},
            {rankTypeCatalogCode, "GM", "总经理"}, {rankTypeCatalogCode, "PM", "副总经理"}, {rankTypeCatalogCode, "YG", "普通员工"}};
        batchSaveDataDictionary(rankTypeElements);

        //用户岗位类型字典 stationType
        DataCatalog stationTypeDataCatalog = defaultDataCatalog(userCode, topUnitCode,
            DATA_CATALOG_STATION_SUFFIX, "用户岗位");
        dataCatalogDao.saveNewObject(stationTypeDataCatalog);
        String[][] stationTypeElements = {{stationTypeDataCatalog.getCatalogCode(), "PT", "普通岗位"}, {stationTypeDataCatalog.getCatalogCode(), "LD", "领导岗位"}};
        batchSaveDataDictionary(stationTypeElements);
    }

    /**
     * 获取一个默认的DataCatalog对象
     *
     * @param createUser        创建人
     * @param topUnitCode       单位code
     * @param catalogCodeSuffix catalog后缀
     * @param catalogName       字典名称
     * @return 默认的DataCatalog对象
     */
    private DataCatalog defaultDataCatalog(String createUser, String topUnitCode,
                                           String catalogCodeSuffix, String catalogName) {
        DataCatalog dataCatalog = new DataCatalog();
        dataCatalog.setCreator(createUser);
        dataCatalog.setCatalogName(catalogName);
        dataCatalog.setTopUnit(topUnitCode);
        dataCatalog.setCatalogCode(topUnitCode + catalogCodeSuffix);
        return dataCatalog;
    }

    /**
     * 批量保存字典数据
     *
     * @param elements 字典数据中的元素值
     */
    private void batchSaveDataDictionary(String[]... elements) {
        if (null == elements || elements.length == 0) {
            return;
        }
        for (String[] element : elements) {
            if (element.length < 3) {
                throw new ObjectException("保存字段数据出错，期待参数个数为3，实际为" + element.length);
            }
            dataDictionaryDao.saveNewObject(defaultDataDictionary(element[0], element[1], element[2]));
        }

    }

    /**
     * 生成一个默认的DataDictionary对象
     *
     * @param catalogCode 字典大类code
     * @param dataCode    字典code
     * @param dataValue   字典值
     * @return 新的DataDictionary对象
     */
    private DataDictionary defaultDataDictionary(String catalogCode, String dataCode, String dataValue) {
        DataDictionary dataDictionary = new DataDictionary();
        DataDictionaryId dataDictionaryId = new DataDictionaryId();
        dataDictionaryId.setCatalogCode(catalogCode);
        dataDictionaryId.setDataCode(dataCode);
        dataDictionary.setId(dataDictionaryId);
        dataDictionary.setDataValue(dataValue);
        return dataDictionary;
    }

    /**
     * 给租户设置默认的资源数量
     *
     * @param tenantInfo    租户基本信息
     * @param oldTenantInfo 需要被持久化的租户基本信息
     */
    private void saveTenantDefaultResourcesAttribute(TenantInfo tenantInfo, TenantInfo oldTenantInfo) {
        if (tenantInfo.getDatabaseNumberLimit() == 0) {
            oldTenantInfo.setDatabaseNumberLimit(databaseNumberLimit);
        }
        if (tenantInfo.getOsNumberLimit() == 0) {
            oldTenantInfo.setOsNumberLimit(osNumberLimit);
        }
        if (tenantInfo.getDataSpaceLimit() == 0) {
            oldTenantInfo.setDataSpaceLimit(dataSpaceLimit);
        }
        if (tenantInfo.getFileSpaceLimit() == 0) {
            oldTenantInfo.setFileSpaceLimit(fileSpaceLimit);
        }
        oldTenantInfo.setPassTime(nowDate());
    }

    /**
     * 补充userinfo中的角色信息
     * @param topUnit 单位code
     * @param userInfoJsonArray 用户信息集合
     * @return
     */
    private List<Map> extendUserRoleByWorkGroup(String topUnit, JSONArray userInfoJsonArray) {
        List<Map> userInfoMap = userInfoJsonArray.toJavaList(Map.class);
        List<String> userCodes = (List<String>) userInfoMap.stream().map(map -> MapUtils.getString(map, "userCode")).collect(Collectors.toList());
        Map<String, Object> hashMap = CollectionsOpt.createHashMap("groupId", topUnit);
        hashMap.put("userCode_in",CollectionsOpt.listToArray(userCodes));
        List<WorkGroup> workGroups = workGroupDao.listObjectsByProperties(hashMap);

        for (Map map : userInfoMap) {
            WorkGroup workGroup = getWorkGroupByUserCodeAndTopUnit(MapUtils.getString(map, "userCode"),topUnit, workGroups);
            if (null == workGroup || StringUtils.isBlank(workGroup.getRoleCode())
                || workGroup.getRoleCode().equals(TENANT_NORMAL_MEMBER_ROLE_CODE)){
                map.put("roleCode",TENANT_NORMAL_MEMBER_ROLE_CODE);
                map.put("roleName","组员");
            }else {
                map.put("roleCode",workGroup.getRoleCode());
                map.put("roleName","管理员");
            }
        }
        return userInfoMap;
    }
    /**
     * 初始化租户信息
     * @param newTenantInfo 租户详情
     * @param oldTenantInfo 租户详情
     */
    private void initTenant(TenantInfo newTenantInfo,TenantInfo oldTenantInfo){
        saveTenantDefaultResourcesAttribute(newTenantInfo, oldTenantInfo);
        tenantInfoDao.updateObject(oldTenantInfo);
        saveTenantRelationData(oldTenantInfo);
    }
}
