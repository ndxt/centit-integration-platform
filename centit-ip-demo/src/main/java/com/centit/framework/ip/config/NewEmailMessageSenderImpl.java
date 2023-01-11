package com.centit.framework.ip.config;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.NoticeMessage;
import com.centit.product.oa.SendMailExecutor;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author tian_y
 */
public class NewEmailMessageSenderImpl implements MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(NewEmailMessageSenderImpl.class);
    private SendMailExecutor emailSender = new SendMailExecutor();
    private String serverEmail;

    public NewEmailMessageSenderImpl() {
    }

    @Override
    public ResponseData sendMessage(String sender, String receiver, NoticeMessage message) {
        HttpServletRequest request = RequestThreadLocal.getLocalThreadWrapperRequest();
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        IUserInfo userinfo = CodeRepositoryUtil.getUserInfoByCode(topUnit, sender);
        String mailFrom;
        if (userinfo == null) {
            mailFrom = this.serverEmail;
        } else {
            mailFrom = userinfo.getRegEmail();
        }

        userinfo = CodeRepositoryUtil.getUserInfoByCode(topUnit, receiver);
        String email = "";
        if (userinfo == null) {
            email = receiver;
            //logger.error("找不到用户：" + receiver);
            //return ResponseData.makeErrorMessage(710, "找不到用户：" + receiver);
        } else {
            email = userinfo.getRegEmail();
        }
        if (email != null && !"".equals(email)) {
            try {
                this.emailSender.sendEmail(new String[]{email}, mailFrom, message.getMsgSubject(), message.getMsgContent(), (List)null);
                return ResponseData.successResponse;
            } catch (EmailException var10) {
                logger.error(var10.getMessage(), var10);
                return ResponseData.makeErrorMessage(var10.getMessage());
            }
        } else {
            return ResponseData.makeErrorMessage(711, "用户：" + receiver + "没有设置注册邮箱");
        }
    }

    public void setHostName(String hostName) {
        this.emailSender.setHostName(hostName);
    }

    public void setSmtpPort(int smtpPort) {
        this.emailSender.setSmtpPort(smtpPort);
    }

    public void setUserName(String userName) {
        this.emailSender.setUserName(userName);
    }

    public void setUserPassword(String userPassword) {
        this.emailSender.setUserPassword(userPassword);
    }

    public void setServerEmail(String serverEmail) {
        this.serverEmail = serverEmail;
    }

}
