package com.factory.controller;

import com.factory.common.Result;
import com.factory.dto.*;
import com.factory.entity.User;
import com.factory.service.UserService;
import com.factory.utils.SecurityUtils;
import com.factory.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public Result<User> getProfile() {
        User user = userService.getById(SecurityUtils.getCurrentUserId());
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> updateProfile(@Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateProfile(SecurityUtils.getCurrentUserId(), updateDTO);
        return Result.success();
    }

    @PutMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> updateAvatar(@RequestParam String avatarUrl) {
        userService.updateAvatar(SecurityUtils.getCurrentUserId(), avatarUrl);
        return Result.success();
    }

    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO passwordDTO) {
        userService.updatePassword(SecurityUtils.getCurrentUserId(), passwordDTO);
        return Result.success();
    }
}