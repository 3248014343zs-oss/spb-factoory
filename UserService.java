package com.factory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.factory.dto.LoginDTO;
import com.factory.dto.PasswordUpdateDTO;
import com.factory.dto.RegisterDTO;
import com.factory.dto.UserUpdateDTO;
import com.factory.entity.User;
import com.factory.vo.LoginVO;

public interface UserService extends IService<User> {
    LoginVO login(LoginDTO loginDTO);
    void register(RegisterDTO registerDTO);
    void updateProfile(Long userId, UserUpdateDTO updateDTO);
    void updateAvatar(Long userId, String avatarUrl);  // 新增
    void updatePassword(Long userId, PasswordUpdateDTO passwordDTO);
}