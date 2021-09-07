package com.centit.framework.users.service;

import com.centit.framework.users.po.Platform;

/**
 * @author zfg
 */
public interface PlatformService {

    Platform getObjectById(String platId);

    void savePlatform(Platform platform);

    void updatePlatform(Platform platform);
}
