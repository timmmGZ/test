package com.gymcn.api.controllers;

import com.gymcn.models.dto.*;
import com.gymcn.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器（登录、注册等）
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;
    
    /**
     * 发送验证码
     */
    @PostMapping("/sms/send")
    public ApiResponse<Void> sendSmsCode(@Valid @RequestBody SendSmsRequest request) {
        userService.sendSmsCode(request.getPhone());
        return ApiResponse.success();
    }
    
    /**
     * 登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ApiResponse.success(response);
    }
}
