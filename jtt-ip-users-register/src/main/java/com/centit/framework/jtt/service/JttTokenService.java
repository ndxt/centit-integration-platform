package com.centit.framework.jtt.service;

import com.centit.framework.jtt.po.JttToken;

/**
 * @author zfg
 */
public interface JttTokenService {

    JttToken getObjectById(String appId);

    void saveAccessToke(JttToken accessToken);

    void updateAccessToken(JttToken accessToken);
}
