package com.yanxuexi.ucenter.service;

import com.yanxuexi.ucenter.model.po.XcUser;

/**
 * @author hyx
 * @version 1.0
 * @description 微信扫码接入
 * @date 2024-09-02 11:57
 **/
public interface WxAuthService {
    /**
     * @description: 微信扫码认证，申请令牌，携带令牌查询用户信息，保存用户信息到数据库
     * @param code 授权码
     * @return 用户信息
     */
    XcUser wxAuth(String code);
}
