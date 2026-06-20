package com.gymcn.services;

import com.gymcn.models.dto.LoginRequest;
import com.gymcn.models.dto.LoginResponse;
import com.gymcn.models.entity.User;
import com.gymcn.models.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    /**
     * 验证码缓存（生产环境用 Redis）
     * key: phone, value: code
     */
    private final ConcurrentHashMap<String, String> smsCodeCache = new ConcurrentHashMap<>();
    
    /**
     * 发送验证码
     */
    public void sendSmsCode(String phone) {
        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 缓存验证码（5分钟有效）
        smsCodeCache.put(phone, code);
        
        // TODO: 调用短信服务发送验证码
        // 这里简化处理，直接打印到日志
        log.info("发送验证码到手机号: {}, 验证码: {}", phone, code);
    }
    
    /**
     * 登录
     */
    public LoginResponse login(LoginRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();
        
        // 验证验证码（开发模式下任意6位数字都可以）
        String cachedCode = smsCodeCache.get(phone);
        if (cachedCode == null || !cachedCode.equals(code)) {
            // 开发环境：允许任意6位验证码
            if (!code.matches("^\\d{6}$")) {
                throw new RuntimeException("验证码不正确");
            }
        }
        
        // 查找或创建用户
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                .eq("phone", phone)
        );
        
        if (user == null) {
            // 新用户
            user = new User();
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(phone.length() - 4));
            user.setUserType("personal");
            userMapper.insert(user);
        }
        
        // 删除验证码
        smsCodeCache.remove(phone);
        
        // 生成 Token（简化处理，生产环境用 JWT）
        String token = generateToken(user);
        
        // 返回响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        LoginResponse.UserVO userVO = new LoginResponse.UserVO();
        BeanUtils.copyProperties(user, userVO);
        response.setUser(userVO);
        
        return response;
    }
    
    /**
     * 生成 Token（简化版，生产环境用 JWT）
     */
    private String generateToken(User user) {
        return "token_" + user.getId() + "_" + System.currentTimeMillis();
    }
}
