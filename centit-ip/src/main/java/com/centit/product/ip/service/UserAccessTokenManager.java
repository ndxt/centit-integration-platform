package com.centit.product.ip.service;

import java.util.List;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.product.ip.po.UserAccessToken;

public interface UserAccessTokenManager extends BaseEntityManager<UserAccessToken,String> {
  
	public UserAccessToken	createNewAccessToken(String userCode);
	
	public List<UserAccessToken> listAccessTokenByUser(String userCode);
}
