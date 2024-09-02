package com.yanxuexi.ucenter.service;

import com.yanxuexi.ucenter.model.dto.AuthParamsDto;
import com.yanxuexi.ucenter.model.po.XcUserExt;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author hyx
 * @version 1.0
 * @description 认证服务接口
 * @date 2024-09-01 20:07
 **/
public interface AuthService {
    /**
     * @description: 认证服务方法
     * @param authParamsDto 认证信息
     * @return XcUserExt
     */
    XcUserExt excute(AuthParamsDto authParamsDto);
}
