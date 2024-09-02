package com.yanxuexi.ucenter.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxuexi.ucenter.mapper.XcUserMapper;
import com.yanxuexi.ucenter.mapper.XcUserRoleMapper;
import com.yanxuexi.ucenter.model.dto.AuthParamsDto;
import com.yanxuexi.ucenter.model.po.XcUser;
import com.yanxuexi.ucenter.model.po.XcUserExt;
import com.yanxuexi.ucenter.model.po.XcUserRole;
import com.yanxuexi.ucenter.service.AuthService;
import com.yanxuexi.ucenter.service.WxAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Max;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author hyx
 * @version 1.0
 * @description
 * @date 2024-09-02 10:50
 **/
@Service("wx_authservice")
@Slf4j
public class WxAuthServiceImpl implements AuthService, WxAuthService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Value("${weixin.appid}")
    String wxAppId;

    @Value("${weixin.secret}")
    String wxSecret;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    @Lazy
    WxAuthServiceImpl currentProxy;

    @Override
    public XcUserExt excute(AuthParamsDto authParamsDto) {
        // 验证账号
        String userName = authParamsDto.getUsername();
        // 根据账号查询数据库表
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(StringUtils.isNotBlank(userName), XcUser::getUsername, userName));
        // 查询结果为空返回null
        if (xcUser == null) {
            throw new RuntimeException("账号不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        return xcUserExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        // 申请令牌，携带令牌查询用户信息，保存用户信息到数据库
        // 申请令牌
        Map<String, String> accessTokenMap = getAccess_token(code);
        // 携带令牌获取用户信息
        String accessToken = accessTokenMap.get("access_token");
        String openid = accessTokenMap.get("openid");
        Map<String, String> userinfo = getUserinfo(accessToken, openid);
        // 将用户信息保存到数据库
        return currentProxy.addWxUser(userinfo);
    }

    /**
     * @param code 授权码
     * @return 令牌
     * @description: 申请访问令牌
     */
    private Map<String, String> getAccess_token(String code) {
        String wxUrlTemplate = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String wxUrl = String.format(wxUrlTemplate, wxAppId, wxSecret, code);
        log.info("调用微信接口申请access_token, url:{}", wxUrl);
        ResponseEntity<String> exchange = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);
        String result = exchange.getBody();
        log.info("调用微信接口申请access_token: 返回值:{}", result);
        Map<String, String> resultMap = JSON.parseObject(result, Map.class);
        return resultMap;
    }

    /**
     * @param access_token 令牌
     * @param openid       openId
     * @return 用户信息
     * @description: 携带令牌获取用户信息
     */
    private Map<String, String> getUserinfo(String access_token, String openid) {
        String wxUrl_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        //请求微信地址
        String wxUrl = String.format(wxUrl_template, access_token, openid);
        log.info("调用微信接口申请access_token, url:{}", wxUrl);
        ResponseEntity<String> exchange = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);
        //防止乱码进行转码
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        log.info("调用微信接口申请access_token: 返回值:{}", result);
        Map<String, String> resultMap = JSON.parseObject(result, Map.class);
        return resultMap;
    }

    /**
     * @description: 保存用户信息
     * @param userInfo_map 用户信息
     * @return 数据库中存放的用户信息
     */
    @Transactional
    public XcUser addWxUser(Map<String, String> userInfo_map) {
        String unionid = userInfo_map.get("unionid").toString();
        //根据unionid查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        if(xcUser!=null){
            return xcUser;
        }
        String userId = UUID.randomUUID().toString();
        xcUser = new XcUser();
        xcUser.setId(userId);
        xcUser.setWxUnionid(unionid);
        //记录从微信得到的昵称
        xcUser.setNickname(userInfo_map.get("nickname").toString());
        xcUser.setUserpic(userInfo_map.get("headimgurl").toString());
        xcUser.setName(userInfo_map.get("nickname").toString());
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setUtype("101001");//学生类型（一定是学生？写死的吗？）
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色（一定是学生？写死的吗？）
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }
}
