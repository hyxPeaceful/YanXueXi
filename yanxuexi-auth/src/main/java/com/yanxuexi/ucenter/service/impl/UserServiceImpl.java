package com.yanxuexi.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.yanxuexi.ucenter.mapper.XcMenuMapper;
import com.yanxuexi.ucenter.mapper.XcUserMapper;
import com.yanxuexi.ucenter.model.dto.AuthParamsDto;
import com.yanxuexi.ucenter.model.po.XcMenu;
import com.yanxuexi.ucenter.model.po.XcUserExt;
import com.yanxuexi.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 获取用户信息 UserDetails
 * @date 2024-09-01 15:58
 **/
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    XcMenuMapper xcMenuMapper;

    /**
     * @description: 根据账号查询用户信息
     * @param authParamsJson 认证参数
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String authParamsJson) throws UsernameNotFoundException {
        // 解析认证参数
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(authParamsJson, AuthParamsDto.class);
        } catch (Exception e) {
            log.error("认证请求数据格式不对, {}", authParamsDto);
            throw new RuntimeException("认证请求数据格式不对");
        }
        // 认证（策略模式）
        String authType = authParamsDto.getAuthType(); // 获取认证类型
        AuthService authService = applicationContext.getBean(authType + "_authservice", AuthService.class);
        XcUserExt xcUserExt = authService.excute(authParamsDto);

        return getUserPrinciple(xcUserExt);
    }

    /**
     * @description: 根据xcUserExt获取UserDetails
     * @param xcUserExt 认证后返回的信息
     * @return UserDetails
     */
    private UserDetails getUserPrinciple(XcUserExt xcUserExt) {
        // 获取用户信息，封装成UserDetails返回
        // 查询用户权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(xcUserExt.getId());
        String[] authorities = xcMenus.stream().map(XcMenu::getCode).toArray(String[]::new);
        String password = xcUserExt.getPassword();
        // 封装用户信息，作为JWT的负载部分，去除其中的敏感信息
        xcUserExt.setPassword(null);
        String userInfo = JSON.toJSONString(xcUserExt);
        return User.withUsername(userInfo).password(password).authorities(authorities).build();
    }
}
