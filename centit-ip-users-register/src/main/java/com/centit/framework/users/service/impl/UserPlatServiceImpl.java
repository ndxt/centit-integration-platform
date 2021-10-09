package com.centit.framework.users.service.impl;

import com.centit.framework.users.dao.UserPlatDao;
import com.centit.framework.users.po.UserPlat;
import com.centit.framework.users.service.UserPlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author zfg
 */
@Service("userPlatService")
public class UserPlatServiceImpl implements UserPlatService {

    @Autowired
    private UserPlatDao userPlatDao;

    @Override
    public void mergeObject(UserPlat userPlat) {
        userPlatDao.mergeObject(userPlat);
    }

    @Override
    public void deleteObjectById(String userPlatId) {
        userPlatDao.deleteObjectById(userPlatId);
    }

    @Override
    public UserPlat getObjectById(String userPlatId) {
        return userPlatDao.getObjectById(userPlatId);
    }

    @Override
    public UserPlat getUserPlatByProperties(Map<String, Object> paramsMap) {
        return userPlatDao.getObjectByProperties(paramsMap);
    }

    @Override
    public void saveUserPlat(UserPlat userPlat) {
        userPlatDao.saveNewObject(userPlat);
    }

    @Override
    public void updateUserPlat(UserPlat userPlat) {
        userPlatDao.updateObject(userPlat);
    }
}
