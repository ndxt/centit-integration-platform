package com.centit.framework.cas.utils;

import com.centit.support.security.Md5Encoder;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * org.springframework.security.crypto.password
 */
public class SimpleMd5PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return Md5Encoder.encode(String.valueOf(rawPassword));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return StringUtils.equals(
                    Md5Encoder.encode(String.valueOf(rawPassword)),
                    encodedPassword );
    }
}
