package com.centit.framework.ip.service;

import java.util.List;

import com.centit.framework.hibernate.service.BaseEntityManager;
import com.centit.framework.ip.po.UserAccessToken;

public interface UserAccessTokenManager extends BaseEntityManager<UserAccessToken,String> {
  
	public UserAccessToken	createNewAccessToken(String userCode);
	
	public List<UserAccessToken> listAccessTokenByUser(String userCode);
}
