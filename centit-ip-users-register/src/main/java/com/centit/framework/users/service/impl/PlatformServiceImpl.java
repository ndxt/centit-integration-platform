package com.centit.framework.users.service.impl;

import com.centit.framework.users.dao.PlatformDao;
import com.centit.framework.users.po.Platform;
import com.centit.framework.users.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zfg
 */
@Service("platformService")
@Transactional
public class PlatformServiceImpl implements PlatformService {

    @Autowired
    private PlatformDao platformDao;

    @Override
    public Platform getObjectById(String platId) {
        return platformDao.getObjectById(platId);
    }

    @Override
    public void savePlatform(Platform platform) {
        platformDao.saveNewObject(platform);
    }

    @Override
    public void updatePlatform(Platform platform) {
        platformDao.updateObject(platform);
    }
}
