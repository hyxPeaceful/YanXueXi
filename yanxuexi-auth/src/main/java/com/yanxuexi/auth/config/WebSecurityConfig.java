package com.yanxuexi.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author Mr.M
 * @version 1.0
 * @description 安全管理配置
 * @date 2022/9/26 20:53
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // 配置认证管理Bean
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt加密（用户输入密钥明文，经过BCrypt加密后与数据库中存储的密钥核对）
        return new BCryptPasswordEncoder();
    }

    @Autowired
    DaoAuthenticationProviderCustom daoAuthenticationProviderCustom;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 配置我们自定义的验证流程
        auth.authenticationProvider(daoAuthenticationProviderCustom);
    }

    //配置安全拦截机制
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/r/**").authenticated()//访问/r开始的请求需要认证通过
                .anyRequest().permitAll()//其它请求全部放行
                .and()
                .formLogin().successForwardUrl("/login-success");//登录成功跳转到/login-success
    }
}
