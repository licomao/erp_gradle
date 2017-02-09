package com.daqula.carmore;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * {@link ConfigurationProperties} for configuring Garageman
 */
@Component
@ConfigurationProperties(prefix = "com.daqula.carmore")
public class CarmoreProperties {

    private boolean createOrderByWebHook;

    /** ping++ 管理平台对应的API key */
    private String pingppApiKey;

    /** ping++ 管理平台对应的应用ID */
    private String pingppAppId;

    /** 腾讯信鸽ACCESS_ID */
    private String xingGeAccessId;

    /** 腾讯信鸽SECRET_KEY */
    private String xingGeSecretKey;

    /** 手机验证码服务器地址 */
    private String SMSUri;

    /** 手机验证码服务器用户名 */
    private String SMSUser;

    /** 手机验证码服务器密码 */
    private String SMSPass;

    /** Debug 开关 */
    private boolean debug;

    /** 手机验证码有效时间 */
    private int verifyCodeExpireTimeInMinutes;

    /** 是否使用JPAFixture创建初始化数据 */
    private boolean loadJPAFixture;

    public boolean isCreateOrderByWebHook() {
        return createOrderByWebHook;
    }

    public void setCreateOrderByWebHook(boolean createOrderByWebHook) {
        this.createOrderByWebHook = createOrderByWebHook;
    }

    public String getPingppApiKey() {
        return pingppApiKey;
    }

    public void setPingppApiKey(String pingppApiKey) {
        this.pingppApiKey = pingppApiKey;
    }

    public String getPingppAppId() {
        return pingppAppId;
    }

    public void setPingppAppId(String pingppAppId) {
        this.pingppAppId = pingppAppId;
    }

    public String getXingGeAccessId() {
        return xingGeAccessId;
    }

    public void setXingGeAccessId(String xingGeAccessId) {
        this.xingGeAccessId = xingGeAccessId;
    }

    public String getXingGeSecretKey() {
        return xingGeSecretKey;
    }

    public void setXingGeSecretKey(String xingGeSecretKey) {
        this.xingGeSecretKey = xingGeSecretKey;
    }

    public String getSMSUri() {
        return SMSUri;
    }

    public void setSMSUri(String SMSUri) {
        this.SMSUri = SMSUri;
    }

    public String getSMSUser() {
        return SMSUser;
    }

    public void setSMSUser(String SMSUser) {
        this.SMSUser = SMSUser;
    }

    public String getSMSPass() {
        return SMSPass;
    }

    public void setSMSPass(String SMSPass) {
        this.SMSPass = SMSPass;
    }

    public int getVerifyCodeExpireTimeInMinutes() {
        return verifyCodeExpireTimeInMinutes;
    }

    public void setVerifyCodeExpireTimeInMinutes(int verifyCodeExpireTimeInMinutes) {
        this.verifyCodeExpireTimeInMinutes = verifyCodeExpireTimeInMinutes;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isLoadJPAFixture() {
        return loadJPAFixture;
    }

    public void setLoadJPAFixture(boolean loadJPAFixture) {
        this.loadJPAFixture = loadJPAFixture;
    }
}