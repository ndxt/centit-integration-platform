package com.centit.framework.cas.controller;


import com.centit.support.image.CaptchaImageUtil;
import org.apereo.cas.web.AbstractDelegateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CaptchaController extends AbstractDelegateController {
    private static Logger logger = LoggerFactory.getLogger(CaptchaController.class);
    public CaptchaController() {
    }

    @Override
    public boolean canHandle(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @GetMapping(value = "/captcha", produces = "image/gif")
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //设置response头信息
        //禁止缓存
        String checkcode = CaptchaImageUtil.getRandomString();
        request.getSession().setAttribute(
            CaptchaImageUtil.SESSIONCHECKCODE, checkcode);

        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("image/gif");
        //存储验证码到session
        BufferedImage image = CaptchaImageUtil.generateCaptchaImage(checkcode);
        try(ServletOutputStream os = response.getOutputStream()) {
            ImageIO.write(image, "gif", os);
            os.flush();
        }catch(IOException e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }
}
