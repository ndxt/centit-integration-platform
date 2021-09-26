package com.centit.framework.tenan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.ResponseData;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.basedata.IDataDictionary;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.framework.system.dao.OsInfoDao;
import com.centit.framework.system.dao.UnitInfoDao;
import com.centit.framework.system.dao.UserInfoDao;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.system.po.OsInfo;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserUnit;
import com.centit.framework.tenan.dao.*;
import com.centit.framework.tenan.po.*;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.tenan.service.TenantPowerManage;
import com.centit.framework.tenan.service.TenantService;
import com.centit.framework.tenan.vo.PageListTenantInfoQo;
import com.centit.framework.tenan.vo.TenantMemberApplyVo;
import com.centit.framework.tenan.vo.TenantMemberQo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.centit.framework.tenan.constant.TenantConstant.APPLICATION_ADMIN_ROLE_CODE;
import static com.centit.framework.tenan.util.UserUtils.getUserCodeFromSecurityContext;

@Service
@Slf4j
public class TenantServiceImpl implements TenantService {

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
    private TenantMemberDao tenantMemberDao;

    @Autowired
    private OsInfoDao osInfoDao;

    @Override
    @Transactional
    public ResponseData registerUserAccount(UserInfo userInfo) throws IllegalAccessException {

        String userPwd = userInfo.getUserPwd();
        if (StringUtils.isBlank(userPwd)) {
            return ResponseData.makeErrorMessage("用户密码不能为空");
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

        saveTenantInfo(tenantInfo);
        return ResponseData.makeSuccessResponse("租户申请成功,等待平台管理员审核!");
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
        String code = MapUtils.getString(parameters, "code");
        String codeType = MapUtils.getString(parameters, "codeType");
        String approveType = MapUtils.getString(parameters, "approveType");
        if (StringUtils.isAnyBlank(code, codeType, approveType)) {
            throw new ObjectException("缺少参数code,codeType,approveType;");
        }

        List<TenantMemberApply> tenantMemberApplies = listApplyInfoByCondition(pageDesc, code, codeType, approveType);
        if (CollectionUtils.sizeIsEmpty(tenantMemberApplies)) {
            return PageQueryResult.createResult(Collections.emptyList(), pageDesc);
        }

        return PageQueryResult.createResult(formatMemberApply(tenantMemberApplies), pageDesc);
    }


    @Override
    @Transactional
    public ResponseData adminCheckTenant(TenantInfo tenantInfo) {

        if (StringUtils.isBlank(tenantInfo.getTopUnit())) {
            return ResponseData.makeErrorMessage("topUnit字段属性为空");
        }

        if (!equalsAny(tenantInfo.getIsAvailable(), "T", "F")) {
            return ResponseData.makeErrorMessage("isAvailable字段属性有误");
        }
        TenantInfo oldTenantInfo = tenantInfoDao.getObjectById(tenantInfo.getTopUnit());
        oldTenantInfo.setMemo(tenantInfo.getMemo());
        oldTenantInfo.setIsAvailable(tenantInfo.getIsAvailable());
        oldTenantInfo.setUpdateTime(nowDate());
        tenantInfo.setUpdator(getUserCodeFromSecurityContext());
        if ("F".equals(tenantInfo.getIsAvailable())) {
            oldTenantInfo.setPassTime(null);
        } else {
            oldTenantInfo.setPassTime(nowDate());
        }

        tenantInfoDao.updateObject(oldTenantInfo);
        if ("T".equals(tenantInfo.getIsAvailable())) {
            //根据租户信息创建机构信息的和租户与机构的关系
            // 租户所有者不需要分配权限，根据当前用户是否为租户所有者判断是否具有资源申请，租户转让的能力
            UnitInfo unitInfo = saveUnitInfoByTenantInfo(oldTenantInfo);
            saveUserUnitByTenantAndUnit(oldTenantInfo, unitInfo);
        }
        return ResponseData.makeSuccessResponse();
    }

    @Override
    @Transactional
    public ResponseData agreeJoin(TenantMemberApplyVo tenantMemberApplyVo) {
        String applyState = tenantMemberApplyVo.getApplyState();
        if (equalsAny(applyState, "3", "4")) {
            return ResponseData.makeErrorMessage("applyState属性值有误");
        }

        TenantMemberApply tenantMemberApply = new TenantMemberApply();
        BeanUtils.copyProperties(tenantMemberApplyVo, tenantMemberApply);
        tenantMemberApplyDao.updateObject(tenantMemberApply);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserCode(tenantMemberApplyVo.getUserCode());
        userInfo.setIsValid("T");
        userInfoDao.updateObject(userInfo);
        //如果当前操作人是租户管理员，且同意了用户的申请，则给用户分给机构
        saveTenantUserUnit(tenantMemberApplyVo.getOptUserType(), tenantMemberApply);
        return ResponseData.makeSuccessResponse();
    }


    @Override
    @Transactional
    public ResponseData updateUserInfo(UserInfo userinfo) {
        if (StringUtils.isBlank(userinfo.getUserCode())) {
            return ResponseData.makeErrorMessage("userCode不能为空");
        }
        if (!userinfo.getUserCode().equals(getUserCodeFromSecurityContext())) {
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
        return ResponseData.makeSuccessResponse();
    }

    @Override
    @Transactional
    public ResponseData quitTenant(String topUnit, String userCode) {
        if (tenantInfoDao.userIsOwner(topUnit, userCode)) {
            return ResponseData.makeErrorMessage("租户所有者不允许退出租户");
        }
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
        return ResponseData.makeSuccessResponse("已退出该机构!");
    }


    @Override
    public ResponseData businessTenant(TenantBusinessLog tenantBusinessLog) {

        TenantInfo tenantInfo = tenantInfoDao.getObjectById(tenantBusinessLog.getTopUnit());
        if (tenantInfo == null || !tenantBusinessLog.getAssignorUserCode().equals(tenantInfo.getOwnUser())) {
            //如果租户所有人和转让人不是同一个人停止交易
            return ResponseData.makeSuccessResponse("无权转让该租户!");
        }

        String assignorUserCode = tenantBusinessLog.getAssignorUserCode();
        String assigneeUserCode = tenantBusinessLog.getAssigneeUserCode();
        List<String> userCodes = CollectionsOpt.createList(assignorUserCode, assigneeUserCode);
        List<UserInfo> userInfos = getUserInfosByUserCodes(userCodes);
        UserInfo assigneeUserInfo = getUserInfoByUserCode(userInfos, assigneeUserCode);
        if (null == assigneeUserInfo) {
            return ResponseData.makeSuccessResponse("受让人信息有误!");
        }
        tenantBusinessLog.setAssigneeUserName(assigneeUserInfo.getUserName());
        tenantBusinessLog.setAssignorUserName(getUserInfoByUserCode(userInfos, assignorUserCode).getUserName());
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

        List<TenantInfo> tenantInfos = tenantInfoDao.listObjectsByProperties(tenantInfo, pageDesc);
        return PageQueryResult.createResult(tenantInfos, pageDesc);
    }

    @Override
    public PageQueryResult<TenantMember> pageListTenantMember(TenantMemberQo tenantMemberQo, PageDesc pageDesc) {
        if (StringUtils.isBlank(tenantMemberQo.getTopUnit())) {
            throw new ObjectException("topUnit 不能为空");
        }
        return PageQueryResult.createResult(tenantMemberDao.pageListTenantMember(tenantMemberQo, pageDesc), pageDesc);
    }

    @Override
    @Transactional
    public ResponseData adminAssignRole(TenantMemberQo tenantMemberQo) {
        boolean check = StringUtils.isBlank(tenantMemberQo.getRoleCode())
            || StringUtils.isBlank(tenantMemberQo.getMemberUserCode())
            || StringUtils.isBlank(tenantMemberQo.getTopUnit());
        if (check) {
            return ResponseData.makeErrorMessage("参数roleCode,topUnit,memberUserCode不能为空!");
        }
        String topUnit = tenantMemberQo.getTopUnit();
        String currentUserCode = getUserCodeFromSecurityContext();
        boolean userHasPower = null != currentUserCode
            && (tenantInfoDao.userIsOwner(topUnit, currentUserCode) || workGroupDao.userIsTenantAdmin(topUnit, currentUserCode));
        if (!userHasPower) {
            return ResponseData.makeErrorMessage("当前人员没有操作权限!");
        }
        if (!equalsAny(tenantMemberQo.getRoleCode(), "ZHGLY", "ZHZY")) {
            return ResponseData.makeErrorMessage("角色代码有误!");
        }
        updateWorkGroupRole(tenantMemberQo, topUnit, currentUserCode);
        return ResponseData.makeSuccessResponse("操作成功!");
    }

    @Override
    public ResponseData userTenants(String userCode) {
        if (StringUtils.isBlank(userCode)) {
            return ResponseData.makeErrorMessage("userCode 不能为空");
        }
        List<TenantInfo> tenantInfos = userTenantsByUserCode(userCode);
        if (CollectionUtils.sizeIsEmpty(tenantInfos)) {
            return ResponseData.makeResponseData(Collections.emptyMap());
        }
        return ResponseData.makeResponseData(formatTenants(userCode, tenantInfos));
    }

    @Override
    @Transactional
    public ResponseData createApplication(OsInfo osInfo) {
        String topUnit = osInfo.getTopUnit();
        String osName = osInfo.getOsName();
        String created = osInfo.getCreated();
        if (StringUtils.isAnyBlank(topUnit, osName, created)) {
            return ResponseData.makeErrorMessage("topUnit,osName,created不能为空");
        }
        List<WorkGroup> workGroups = workGroupDao.listWorkGroupByUserCodeAndTopUnit(created, topUnit);
        if (CollectionUtils.sizeIsEmpty(workGroups) || !workGroupDao.userIsTenantAdmin(topUnit, created)) {
            return ResponseData.makeErrorMessage("该用户没有创建应用的权限,请使用管理员账号创建应用");
        }
        osInfo.setOsType("P");
        osInfo.setCreateTime(nowDate());
        saveWorkGroupByOSInfo(osInfo);
        return ResponseData.makeSuccessResponse("应用创建成功!");
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
    public ResponseData userInWorkGroup(WorkGroup workGroup) {
        String groupId = workGroup.getGroupId();
        String userCode = workGroup.getUserCode();
        if (StringUtils.isAnyBlank(groupId, userCode)) {
            return ResponseData.makeErrorMessage("参数groupId,userCode不能为空");
        }
        return ResponseData.makeResponseData(workGroupDao.userIsMember(groupId, userCode));
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
        //todo:查看用户是否在该机构内

        String unitCode = MapUtils.getString(paramMap, "unitCode");
        String userName = MapUtils.getString(paramMap, "userName");
        String regCellPhone = MapUtils.getString(paramMap, "regCellPhone");
        String userCode = MapUtils.getString(paramMap, "userCode");
        if (StringUtils.isAllBlank(userName,regCellPhone,userCode)){
            return ResponseData.makeErrorMessage("userName,regCellPhone,userCode不能全为空");
        }
        if (StringUtils.isBlank(unitCode)) {
            return ResponseData.makeErrorMessage("unitCode不能为空");
        }
        List<UserInfo> userInfos = getUserInfosByExactUserName(paramMap);
        if (CollectionUtils.sizeIsEmpty(userInfos)) {
            return ResponseData.makeResponseData(CollectionUtils.emptyCollection());
        }
        List<UserUnit> userUnits = userUnitDao.listObjects(CollectionsOpt.createHashMap("topUnit", unitCode));
        Iterator<UserInfo> iterator = userInfos.iterator();
        while (iterator.hasNext()){
            UserUnit userUnitByUserCode = getUserUnitByUserCode(userUnits, iterator.next().getUserCode());
            if (null != userUnitByUserCode){
                iterator.remove();
            }
        }
        return ResponseData.makeResponseData(userInfos);
    }

    /**
     * 通过userCode userName regCellPhone精确查找用户
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
        workGroup.setGroupId(osInfo.getOsId());
        workGroup.setUserCode(osInfo.getCreated());
        workGroup.setRoleCode(APPLICATION_ADMIN_ROLE_CODE);
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
        Set<String> topUnitCodes = tenantInfos.stream().map(TenantInfo::getTopUnit).collect(Collectors.toSet());
        List<WorkGroup> workGroups = workGroupDao.listWorkGroupByUserCodeAndTopUnit(userCode, CollectionsOpt.listToArray(topUnitCodes));
        String primaryUnit = userInfoDao.getUserByCode(userCode).getPrimaryUnit();
        List<Map> tenantJsonArrays = JSONArray.parseArray(JSONArray.toJSONString(tenantInfos), Map.class);
        for (Map tenantJson : tenantJsonArrays) {
            extendTenantsAttribute(userCode, workGroups, primaryUnit, tenantJson);
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
    private void extendTenantsAttribute(String userCode, List<WorkGroup> workGroups, String primaryUnit, Map tenantJson) {
        if (MapUtils.getString(tenantJson, "ownUser").equals(userCode)) {
            tenantJson.put("isOwner", "T");
        } else {
            tenantJson.put("isOwner", "F");
        }

        String topUnit = MapUtils.getString(tenantJson, "topUnit");
        WorkGroup workGroup = getWorkGroupByUserCodeAndTopUnit(userCode, topUnit, workGroups);
        if (null != workGroup) {
            tenantJson.put("roleCode", workGroup.getRoleCode());
        } else {
            tenantJson.put("roleCode", "");
        }

        if (MapUtils.getString(tenantJson, "topUnit").equals(primaryUnit)) {
            tenantJson.put("primaryUnit", "T");
        } else {
            tenantJson.put("primaryUnit", "F");
        }
        List<UserUnit> userUnits = userUnitDao.listObjects(CollectionsOpt.createHashMap("userCode", userCode, "topUnit", topUnit));
        extendTenantsUserRanks(tenantJson, userUnits);

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
            return;
        }
        List<String> userRanks = userUnits.stream().filter(userUnit -> StringUtils.isNotBlank(userUnit.getUserRank()))
            .map(UserUnit::getUserRank).collect(Collectors.toList());
        tenantJson.put("userRank", Optional.ofNullable(userRanks).orElse(new ArrayList<>()));
        if (CollectionUtils.sizeIsEmpty(userRanks)) {
            tenantJson.put("userRankText", new ArrayList<>());
            return;
        }
        ArrayList<String> userRankTexts = new ArrayList<>();
        for (String userRank : userRanks) {
            IDataDictionary rankType = CodeRepositoryUtil.getDataPiece("RankType", userRank);
            if (null != rankType) {
                userRankTexts.add(rankType.getDataValue());
            }
        }
        tenantJson.put("userRankText", userRankTexts);
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
            if (userCode.equals(workGroup.getUserCode()) && topUnit.equals(workGroup.getGroupId())) {
                return workGroup;
            }
        }
        return null;
    }

    /**
     * 更新角色
     *
     * @param tenantMemberQo
     * @param topUnit
     * @param currentUserCode
     */
    private void updateWorkGroupRole(TenantMemberQo tenantMemberQo, String topUnit, String currentUserCode) {
        WorkGroup workGroup = new WorkGroup();
        workGroup.setGroupId(topUnit);
        workGroup.setUserCode(tenantMemberQo.getMemberUserCode());
        workGroup.setRoleCode(tenantMemberQo.getRoleCode());
        workGroup.setUpdateDate(nowDate());
        workGroup.setUpdator(currentUserCode);
        Map<String, Object> hashMap = CollectionsOpt.createHashMap("groupId", workGroup.getGroupId(), "userCode", workGroup.getUserCode());
        List<WorkGroup> workGroups = workGroupDao.listObjectsByProperties(hashMap);
        if (workGroups.size() > 1) {
            throw new ObjectException("检测到该人员在一个租户中的角色个数大于一个，操作停止!");
        }
        if (workGroups.size() == 1) {
            updateWorkGroupByGroupAndUserCode(tenantMemberQo);
        }
        if (workGroups.size() == 0) {
            workGroup.setCreator(currentUserCode);
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
     * 根据userCode groupId 更新roleCode updator
     *
     * @param tenantMemberQo
     * @return
     */
    private int updateWorkGroupByGroupAndUserCode(TenantMemberQo tenantMemberQo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("groupId", tenantMemberQo.getTopUnit());
        map.put("userCode", tenantMemberQo.getMemberUserCode());
        map.put("roleCode", tenantMemberQo.getRoleCode());
        map.put("updator", getUserCodeFromSecurityContext());
        return workGroupDao.updateByProperties(map);
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
        map.put("g2_regEmail", userInfo.getRegEmail());
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
        tenantInfo.setOwnUser(tenantInfo.getCreator());
        tenantInfo.setTopUnit(UuidOpt.getUuidAsString32());
        tenantInfoDao.saveNewObject(tenantInfo);
        return tenantInfo;
    }


    /**
     * 给租户下的人分配单位
     *
     * @param optUserType
     * @param tenantMemberApply
     */
    private void saveTenantUserUnit(String optUserType, TenantMemberApply tenantMemberApply) {
        TenantMemberApply oldTenantMemberApply = new TenantMemberApply();
        if (tenantMemberApply.getApplyState().equals("3")) {
            oldTenantMemberApply = tenantMemberApplyDao.getObjectById(tenantMemberApply);
        }
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
     * @param userCode 用户code
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
     * 根据条件分页查询申请列表数据
     *
     * @param pageDesc
     * @param code
     * @param codeType
     * @param approveType
     * @return
     */
    private List<TenantMemberApply> listApplyInfoByCondition(PageDesc pageDesc, String code, String codeType, String approveType) {
        if (codeType.equals("1") && approveType.equals("1")) {
            //租户邀请用户，用户待审批数据
            Map<String, Object> maps = CollectionsOpt.createHashMap("userCode", code);
            return tenantMemberApplyDao.pageListNotApproveApplyByUserCode(maps, pageDesc);
        }
        if (codeType.equals("1") && approveType.equals("2")) {
            //租户邀请用户，用户已经审批数据
            Map<String, Object> maps = CollectionsOpt.createHashMap("userCode", code);
            return tenantMemberApplyDao.pageListHasApproveApplyByUserCode(maps, pageDesc);
        }
        if (codeType.equals("2") && approveType.equals("1")) {
            //用户申请加入租户，租户待审批数据
            Map<String, Object> maps = CollectionsOpt.createHashMap("topUnit", code);
            return tenantMemberApplyDao.pageListNotApproveApplyByUnitCode(maps, pageDesc);
        }
        if (codeType.equals("2") && approveType.equals("2")) {
            //用户申请加入租户，租户已经审批数据
            Map<String, Object> maps = CollectionsOpt.createHashMap("topUnit", code);
            return tenantMemberApplyDao.pageListHasApproveApplyByUnitCode(maps, pageDesc);
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
     * @param userCode
     * @return
     */
    private boolean userInTenant(String userCode) {
        List<UserUnit> userUnits = userUnitDao.listObjects(CollectionsOpt.createHashMap("userCode", userCode));
        if (CollectionUtils.sizeIsEmpty(userUnits)) {
            return false;
        }
        Set<String> topUnits = userUnits.stream().map(UserUnit::getTopUnit).collect(Collectors.toSet());
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
        Set<String> topUnits = userUnits.stream().map(UserUnit::getTopUnit).collect(Collectors.toSet());
        return tenantInfoDao.listObjects(CollectionsOpt.createHashMap("topUnit_in",
            CollectionsOpt.listToArray(topUnits)));

    }

}
