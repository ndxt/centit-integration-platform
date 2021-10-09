package com.centit.framework.users.service;

import com.centit.framework.users.po.UserPlat;

import java.util.Map;

/**
 * @author zfg
 */
public interface UserPlatService {

    UserPlat getObjectById(String userPlatId);

    UserPlat getUserPlatByProperties(Map<String,Object> paramsMap);

    void mergeObject(UserPlat userPlat);

    void deleteObjectById(String userPlatId);

    void saveUserPlat(UserPlat userPlat);

    void updateUserPlat(UserPlat userPlat);
}
