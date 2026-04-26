package com.factory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.factory.common.BusinessException;
import com.factory.dto.*;
import com.factory.entity.User;
import com.factory.mapper.UserMapper;
import com.factory.service.UserService;
import com.factory.utils.JwtUtils;
import com.factory.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtils jwtUtils;
    private final InvitationCodeServiceImpl invitationCodeService;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = baseMapper.getByUsername(loginDTO.getUsername());
        if (user == null || !BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }
        String token = jwtUtils.createToken(user);
        return new LoginVO(token, user.getRole(), user.getNickname());
    }

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword(), BCrypt.gensalt()));
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1);
        user.setUid(generateUid());

        String inviteCode = registerDTO.getInviteCode();
        if (inviteCode != null && !inviteCode.isEmpty()) {
            if (invitationCodeService.isBossGodCode(inviteCode)) {
                user.setRole("BOSS");
                baseMapper.insert(user);
                return;
            }
            user.setRole("OPERATOR");
            baseMapper.insert(user);
            try {
                invitationCodeService.useCode(inviteCode, user.getId());
            } catch (Exception e) {
                baseMapper.deleteById(user.getId());
                throw new BusinessException(e.getMessage());
            }
        } else {
            user.setRole("OPERATOR");
            baseMapper.insert(user);
        }
    }

    private Long generateUid() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getUid).last("LIMIT 1");
        User maxUser = baseMapper.selectOne(wrapper);
        return (maxUser == null || maxUser.getUid() == null) ? 100001L : maxUser.getUid() + 1;
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UserUpdateDTO updateDTO) {
        User user = baseMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (updateDTO.getNickname() != null) user.setNickname(updateDTO.getNickname());
        if (updateDTO.getEmail() != null) user.setEmail(updateDTO.getEmail());
        if (updateDTO.getPhone() != null) user.setPhone(updateDTO.getPhone());
        baseMapper.updateById(user);
    }

    @Override
    @Transactional
    public void updateAvatar(Long userId, String avatarUrl) {
        User user = baseMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setAvatar(avatarUrl);
        baseMapper.updateById(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDTO passwordDTO) {
        User user = baseMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (!BCrypt.checkpw(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(BCrypt.hashpw(passwordDTO.getNewPassword(), BCrypt.gensalt()));
        baseMapper.updateById(user);
    }

    public List<User> getEmployeesByManager(Long managerUid, String managerRole) {
        int managerLevel = getRoleLevel(managerRole);
        List<User> allUsers = baseMapper.selectList(new LambdaQueryWrapper<User>()
                .ne(User::getUid, managerUid).eq(User::getStatus, 1));
        return allUsers.stream().filter(u -> getRoleLevel(u.getRole()) > managerLevel).collect(Collectors.toList());
    }

    @Transactional
    public void downgradeEmployee(Long managerUid, String managerRole, Long targetUid) {
        User target = baseMapper.selectByUid(targetUid);
        if (target == null) throw new BusinessException("员工不存在");
        int managerLevel = getRoleLevel(managerRole);
        int targetLevel = getRoleLevel(target.getRole());
        if (targetLevel <= managerLevel) throw new BusinessException("您没有权限降级该员工");
        String newRole;
        switch (target.getRole()) {
            case "ADMIN": newRole = "WAREHOUSE"; break;
            case "WAREHOUSE": newRole = "OPERATOR"; break;
            case "OPERATOR": throw new BusinessException("操作员无法再降级");
            case "BOSS": throw new BusinessException("无法降级老板");
            default: throw new BusinessException("未知角色");
        }
        target.setRole(newRole);
        baseMapper.updateById(target);
    }

    @Transactional
    public void deleteEmployee(Long managerUid, String managerRole, Long targetUid) {
        User target = baseMapper.selectByUid(targetUid);
        if (target == null) throw new BusinessException("员工不存在");
        int managerLevel = getRoleLevel(managerRole);
        int targetLevel = getRoleLevel(target.getRole());
        if (targetLevel <= managerLevel) throw new BusinessException("您没有权限删除该员工");
        target.setStatus(0);
        baseMapper.updateById(target);
    }

    public User selectByUid(Long uid) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, uid).eq(User::getStatus, 1));
    }

    private int getRoleLevel(String role) {
        switch (role) {
            case "BOSS": return 0;
            case "ADMIN": return 1;
            case "WAREHOUSE": return 2;
            case "OPERATOR": return 3;
            default: return 4;
        }
    }
}