package com.centit.framework.jtt.service.impl;

import com.centit.framework.jtt.dao.JttTokenDao;
import com.centit.framework.jtt.po.JttToken;
import com.centit.framework.jtt.service.JttTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zfg
 */
@Service
public class JttTokenServiceImpl implements JttTokenService {

    @Autowired
    private JttTokenDao jttTokenDao;

    @Override
    public JttToken getObjectById(String appId) {
        return jttTokenDao.getObjectById(appId);
    }

    @Override
    public void saveAccessToke(JttToken accessToken) {
        jttTokenDao.mergeObject(accessToken);
    }

    @Override
    public void updateAccessToken(JttToken accessToken) {
        jttTokenDao.updateObject(accessToken);
    }
}
