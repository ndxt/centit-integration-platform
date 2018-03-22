package com.centit.framework.ip.service;

import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.jdbc.service.BaseEntityManager;

import java.util.List;

public interface UserAccessTokenManager extends BaseEntityManager<UserAccessToken,String> {
  
	public UserAccessToken	createNewAccessToken(String userCode);
	
	public List<UserAccessToken> listAccessTokenByUser(String userCode);
}
