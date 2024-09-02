package com.yanxuexi.ucenter.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxuexi.ucenter.feignClient.CheckCodeClient;
import com.yanxuexi.ucenter.mapper.XcUserMapper;
import com.yanxuexi.ucenter.model.dto.AuthParamsDto;
import com.yanxuexi.ucenter.model.po.XcUser;
import com.yanxuexi.ucenter.model.po.XcUserExt;
import com.yanxuexi.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author hyx
 * @version 1.0
 * @description 密码认证方式
 * @date 2024-09-01 20:10
 **/
@Service("password_authservice")
public class PasswordAuthService implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt excute(AuthParamsDto authParamsDto) {
        // 验证码校验
        String checkcode = authParamsDto.getCheckcode(); // 验证码
        String checkcodekey = authParamsDto.getCheckcodekey(); // 验证码key
        if (StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)) {
            throw new RuntimeException("验证码为空");
        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (verify == null || !verify) {
            throw new RuntimeException("验证码输入错误");
        }

        // 验证账号
        String userName = authParamsDto.getUsername();
        // 根据账号查询数据库表
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(StringUtils.isNotBlank(userName), XcUser::getUsername, userName));
        // 查询结果为空返回null
        if (xcUser == null) {
            throw new RuntimeException("账号不存在");
        }

        // 验证密码
        String encryptPassword = xcUser.getPassword();
        String rawPassword = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(rawPassword, encryptPassword);
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        return xcUserExt;
    }
}
