package com.centit.framework.cas.utils;


import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.security.Md5Encoder;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * Created by codefan on 17-1-20.
 */
@SuppressWarnings("deprecation")
public class StandardPasswordEncoder implements PasswordEncoder {
    private int strength;

    public StandardPasswordEncoder() {
        this.strength = 11;
    }

    public StandardPasswordEncoder(int strength) {
        if(strength<5 ||strength >31){
            this.strength = strength;
        }else {
            this.strength = 11;
        }
    }

    @Override
    public String encodePassword(String rawPass, Object salt) {
        return StandardPasswordEncoder.createPassword(rawPass, StringBaseOpt.castObjectToString(salt),strength);
    }

    @Override
    public boolean isPasswordValid(String encodedPassword, String rawPass, Object salt) {
        return StringUtils.equals(
                encodedPassword,
                StandardPasswordEncoder.createPassword(rawPass, StringBaseOpt.castObjectToString(salt),strength));
    }

    public static String createPassword(String password, String salt, int logRounds) {
        try {
            BCrypt b = new BCrypt();
            Method method = BCrypt.class.getDeclaredMethod("crypt_raw", byte[].class,byte[].class,int.class);
            method.setAccessible(true); //没有设置就会报错
            byte[] pb = (byte[]) method.invoke(b,password.getBytes(), salt.getBytes(), logRounds );
            return new String(Hex.encodeHex(pb));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return Md5Encoder.encodePasswordAsJasigCas(password, salt, logRounds);
        }
    }
}
