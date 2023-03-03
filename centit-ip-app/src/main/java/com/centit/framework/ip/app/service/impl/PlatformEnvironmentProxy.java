package com.centit.framework.ip.app.service.impl;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.OptTreeNode;
import com.centit.support.database.utils.PageDesc;
import org.springframework.security.access.ConfigAttribute;

import java.util.List;
import java.util.Map;

/**
 * Created by codefan on 16-12-16.
 */
public class PlatformEnvironmentProxy implements PlatformEnvironment
{
    private List<PlatformEnvironment> evrnMangers;
    public PlatformEnvironmentProxy(){

    }

    public void setEvrnMangers(List<PlatformEnvironment> evrnMangers) {
        this.evrnMangers = evrnMangers;
    }

    /**
     * 获得用户设置参数
     *
     * @param userCode userCode
     * @param paramCode paramCode
     * @return 用户设置参数
     */
    @Override
    public IUserSetting getUserSetting(String userCode, String paramCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            IUserSetting svalue = evrnManger.getUserSetting(userCode,paramCode);
            if(svalue!=null) {
                return svalue;
            }
        }
        return null;
    }

    /**
     * 获取全部个人设置
     *
     * @param userCode 用户编码
     * @return 个人设置列表
     */
    @Override
    public List<? extends IUserSetting> listUserSettings(String userCode) {
        return null;
    }

    @Override
    public void saveUserSetting(IUserSetting userSetting) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            evrnManger.saveUserSetting(userSetting);
        }
    }

    /*@Override
    public void insertOrUpdateMenu(List<? extends IOptInfo> optInfos, List<? extends IOptMethod> optMethods) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            evrnManger.insertOrUpdateMenu(optInfos, optMethods);
        }
    }
*/
    @Override
    public List<? extends IOsInfo> listOsInfos(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOsInfo> osInfos = evrnManger.listOsInfos(topUnit);
            if(osInfos!=null) {
                return osInfos;
            }
        }
        return null;
    }
    @Override
    public IOsInfo getOsInfo(String osId){
        for(PlatformEnvironment evrnManger:evrnMangers){
            IOsInfo osInfo = evrnManger.getOsInfo(osId);
            if(osInfo!=null) {
                return osInfo;
            }
        }
        return null;
    }
    @Override
    public IOsInfo deleteOsInfo(String osId){
        for(PlatformEnvironment evrnManger:evrnMangers){
            IOsInfo osInfo = evrnManger.deleteOsInfo(osId);
            if(osInfo!=null) {
                return osInfo;
            }
        }
        return null;
    }
    @Override
    public IOsInfo updateOsInfo(IOsInfo osInfo){
        for(PlatformEnvironment evrnManger:evrnMangers){
            IOsInfo iOsInfo = evrnManger.updateOsInfo(osInfo);
            if(iOsInfo!=null) {
                return iOsInfo;
            }
        }
        return null;
    }
    @Override
    public IOsInfo addOsInfo(IOsInfo osInfo){
        for(PlatformEnvironment evrnManger:evrnMangers){
            IOsInfo iOsInfo = evrnManger.addOsInfo(osInfo);
            if(iOsInfo!=null) {
                return iOsInfo;
            }
        }
        return null;
    }

    /**
     * 获取用户所有菜单功能
     *
     * @param userCode userCode
     * @param superOptId superOptId
     * @param asAdmin    是否是作为管理员
     * @return 用户所有菜单功能
     */
    @Override
    public List<? extends IOptInfo> listUserMenuOptInfosUnderSuperOptId(
            String userCode, String superOptId, boolean asAdmin) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOptInfo> value =
                    evrnManger.listUserMenuOptInfosUnderSuperOptId(userCode,superOptId,asAdmin);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }
    @Override
    public List<? extends IOptInfo> listMenuOptInfosUnderOsId(String osId) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOptInfo> value =
                evrnManger.listMenuOptInfosUnderOsId(osId);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }
    @Override
    public IOptInfo addOptInfo(IOptInfo optInfo){
        for(PlatformEnvironment evrnManger:evrnMangers){
            IOptInfo iOptInfo = evrnManger.addOptInfo(optInfo);
            if(iOptInfo!=null) {
                return iOptInfo;
            }
        }
        return null;
    }

    @Override
    public IOptInfo updateOptInfo(IOptInfo optInfo) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            evrnManger.updateOptInfo(optInfo);
        }
        return null;
    }


    @Override
    public List<? extends IUserRole> listUserRoles(String topUnit, String userCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserRole> value =
                    evrnManger.listUserRoles(topUnit, userCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public List<? extends IUserRole> listRoleUsers(String topUnit, String roleCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserRole> value =
                    evrnManger.listRoleUsers(topUnit, roleCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public List<? extends IUnitRole> listUnitRoles(String unitCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUnitRole> value =
                    evrnManger.listUnitRoles(unitCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public List<? extends IUnitRole> listRoleUnits(String roleCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUnitRole> value =
                    evrnManger.listRoleUnits(roleCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 修改用户密码
     *
     * @param userCode userCode
     * @param userPassword userPassword
     */
    @Override
    public void changeUserPassword(String userCode, String userPassword) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            evrnManger.changeUserPassword(userCode,userPassword);
        }
    }

    /**
     * 验证用户密码
     *
     * @param userCode userCode
     * @param userPassword userPassword
     * @return boolean 验证用户密码
     */
    @Override
    public boolean checkUserPassword(String userCode, String userPassword) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            if(evrnManger.checkUserPassword(userCode,userPassword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有用户，
     *
     * @return 所有用户，
     */
    @Override
    public List<? extends IUserInfo> listAllUsers(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserInfo> value = evrnManger.listAllUsers(topUnit);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取所有机构
     *
     * @return 所有机构
     */
    @Override
    public List<? extends IUnitInfo> listAllUnits(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUnitInfo> value = evrnManger.listAllUnits(topUnit);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取所有用户和机构关联关系
     *
     * @return 所有用户和机构关联关系
     */
    @Override
    public List<? extends IUserUnit> listAllUserUnits(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserUnit> value = evrnManger.listAllUserUnits(topUnit);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据用户代码获得 用户的所有租户，顶级机构
     *
     * @param userCode userCode
     * @return List 用户所有的机构信息
     */
    @Override
    public List<? extends IUnitInfo> listUserTopUnits(String userCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUnitInfo> value = evrnManger.listUserTopUnits(userCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据用户代码获得 用户所有的机构信息
     *
     * @param userCode userCode
     * @return 用户所有的机构信息
     */
    @Override
    public List<? extends IUserUnit> listUserUnits(String topUnit, String userCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserUnit> value = evrnManger.listUserUnits(topUnit, userCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据机构代码获得 机构所有用户信息
     *
     * @param unitCode unitCode
     * @return 机构所有用户信息
     */
    @Override
    public List<? extends IUserUnit> listUnitUsers(String unitCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserUnit> value = evrnManger.listUnitUsers(unitCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取所有角色信息
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IRoleInfo> listAllRoleInfo(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IRoleInfo> value = evrnManger.listAllRoleInfo(topUnit);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取所有角色和权限对应关系
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IRolePower> listAllRolePower(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IRolePower> value = evrnManger.listAllRolePower(topUnit);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取业务操作信息
     *
     * @return List 业务信息
     */
    @Override
    public List<? extends IOptInfo> listAllOptInfo(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOptInfo> value = evrnManger.listAllOptInfo(topUnit);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public List<? extends IOptInfo> listOptInfoByRole(String roleCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOptInfo> value = evrnManger.listOptInfoByRole(roleCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取操作方法信息
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IOptMethod> listAllOptMethod(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOptMethod> value = evrnManger.listAllOptMethod(topUnit);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public OptTreeNode getSysOptTree() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            OptTreeNode value = evrnManger.getSysOptTree();
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public List<? extends IOptMethod> listOptMethodByRoleCode(String roleCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOptMethod> value = evrnManger.listOptMethodByRoleCode(roleCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public IOptMethod addOptMethod(IOptMethod optMethod) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            IOptMethod value = evrnManger.addOptMethod(optMethod);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public IOptMethod mergeOptMethod(IOptMethod optMethod) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            IOptMethod value = evrnManger.mergeOptMethod(optMethod);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void deleteOptMethod(String optCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            evrnManger.deleteOptMethod(optCode);
        }
    }

    @Override
    public List<ConfigAttribute> getRolesWithApiId(String apiId) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<ConfigAttribute> value = evrnManger.getRolesWithApiId(apiId);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * @return 所有的数据范围定义表达式
     */
    @Override
    public List<? extends IOptDataScope> listAllOptDataScope(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOptDataScope> value = evrnManger.listAllOptDataScope(topUnit);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取所有数据字典类别信息
     *
     * @return 所有数据字典类别信息
     */
    @Override
    public List<? extends IDataCatalog> listAllDataCatalogs(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IDataCatalog> value = evrnManger.listAllDataCatalogs(topUnit);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取所有数据字典类别信息
     *
     * @param catalogCode catalogCode
     * @return 所有数据字典类别信息
     */
    @Override
    public List<? extends IDataDictionary> listDataDictionaries(String catalogCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IDataDictionary> value = evrnManger.listDataDictionaries(catalogCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void deleteDataDictionary(String catalogCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            evrnManger.deleteDataDictionary(catalogCode);
        }
    }

    @Override
    public int[] updateOptIdByOptCodes(String optId, List<String> optCodes) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            int[] value = evrnManger.updateOptIdByOptCodes(optId, optCodes);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public boolean deleteOptInfoByOptId(String optId) {
        boolean isDelete = false;
        for(PlatformEnvironment evrnManger:evrnMangers){
            isDelete = isDelete || evrnManger.deleteOptInfoByOptId(optId);
        }
        return isDelete;
    }

    @Override
    public boolean deleteOptDefAndRolepowerByOptCode(String optCode) {
        boolean isDelete = false;
        for(PlatformEnvironment evrnManger:evrnMangers){
            isDelete = isDelete || evrnManger.deleteOptDefAndRolepowerByOptCode(optCode);
        }
        return isDelete;
    }

    @Override
    public int countUserByTopUnit(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            int value = evrnManger.countUserByTopUnit(topUnit);
            if(value > 0) {
                return value;
            }
        }
        return 0;
    }

    @Override
    public int countUnitByTopUnit(String topUnit) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            int value = evrnManger.countUnitByTopUnit(topUnit);
            if(value > 0) {
                return value;
            }
        }
        return 0;
    }

    @Override
    public List<? extends IWorkGroup> listWorkGroup(Map<String, Object> filterMap, PageDesc pageDesc) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IWorkGroup> value = evrnManger.listWorkGroup(filterMap, pageDesc);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void batchWorkGroup(List<IWorkGroup> workGroups) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            evrnManger.batchWorkGroup(workGroups);
        }
    }

    @Override
    public boolean loginUserIsExistWorkGroup(String osId, String userCode) {
        boolean isExists = false;
        for(PlatformEnvironment evrnManger:evrnMangers){
            isExists = isExists || evrnManger.loginUserIsExistWorkGroup(osId, userCode);
        }
        return isExists;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param loginName loginName
     * @return 用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            CentitUserDetails value = evrnManger.loadUserDetailsByLoginName(loginName);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param userCode userCode
     * @return 用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            CentitUserDetails value = evrnManger.loadUserDetailsByUserCode(userCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }


    @Override
    public IUnitInfo loadUnitInfo(String unitCode){
        for(PlatformEnvironment evrnManger:evrnMangers){
            IUnitInfo value = evrnManger.loadUnitInfo(unitCode);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param regEmail regEmail
     * @return 用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            CentitUserDetails value = evrnManger.loadUserDetailsByRegEmail(regEmail);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param regCellPhone regCellPhone
     * @return 用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            CentitUserDetails value = evrnManger.loadUserDetailsByRegCellPhone(regCellPhone);
            if(value!=null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void updateUserInfo(IUserInfo userInfo) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            evrnManger.updateUserInfo(userInfo);
        }
    }
}
