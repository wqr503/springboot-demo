package com.cn.lp.domain;


/**
 * 验证等级
 * Created by xiaoqing on 2018/10/7.
 */
public enum VerificationLevel {


    /**
     * 密文密码
     */
    CIPHER_PASSWORD(0),

    /**
     * 时效密码
     */
    TIME_PASSWORD(1);

    private int code;

    public static VerificationLevel getLevelByID(int id) {
        for (VerificationLevel level : VerificationLevel.values()) {
            if (level.getID() == id) {
                return level;
            }
        }
        return null;
    }

    public Integer getID() {
        return this.code;
    }

    VerificationLevel(int code) {
        this.code = code;
    }

}
