package com.centit.framework.users.service.impl;

import com.centit.framework.common.ResponseData;
import com.centit.framework.users.dto.JsmotUnitDTO;
import com.centit.framework.users.dto.JsmotUserDTO;
import com.centit.framework.users.service.JsmotSyncService;
import org.springframework.stereotype.Service;

/**
 * @author zfg
 */
@Service
public class JsmotSyncServiceImpl implements JsmotSyncService {

    @Override
    public ResponseData userCreate(String accessToken, JsmotUserDTO userInfo) {
        return null;
    }

    @Override
    public ResponseData unitCreate(String accessToken, JsmotUnitDTO unitInfo) {
        return null;
    }

    @Override
    public ResponseData getUnitInfo(String accessToken, String deptId) {
        return null;
    }
}
