package com.centit.framework.ip.service.impl;

import com.centit.framework.ip.dao.UserAccessTokenDao;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.ip.service.UserAccessTokenManager;
import com.centit.framework.jdbc.service.BaseEntityManagerImpl;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.UuidOpt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("userAccessTokenManager")
@Transactional
public class UserAccessTokenManagerImpl extends BaseEntityManagerImpl<UserAccessToken,String,UserAccessTokenDao>
        implements UserAccessTokenManager {

    //private static final SysOptLog sysOptLog = SysOptLogFactoryImpl.getSysOptLog();

    @Override
    @Resource(name = "userAccessTokenDao")
    public void setBaseDao(UserAccessTokenDao baseDao) {
        super.baseDao = baseDao;
    }

	@Override
	@Transactional
	public UserAccessToken createNewAccessToken(String userCode) {
		UserAccessToken userToken = new UserAccessToken(userCode);
		userToken.setTokenId(UuidOpt.getUuidAsString32());
		userToken.setSecretAccessKey(UuidOpt.getUuidAsString36());
		userToken.setCreateTime(DatetimeOpt.currentUtilDate());
		baseDao.saveNewObject(userToken);
		return userToken;
	}

	/*@Override
	@Transactional
	public List<UserAccessToken> listAccessTokenByUser(String userCode) {
		return baseDao.listObjects("from UserAccessToken where userCode=?",userCode);
	}*/

	//jdbc
	@Override
	@Transactional
	public List<UserAccessToken> listAccessTokenByUser(String userCode) {
		return baseDao.listObjectsByProperty("userCode",userCode);
	}

}

