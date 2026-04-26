package com.factory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.factory.common.BusinessException;
import com.factory.entity.InvitationCode;
import com.factory.entity.User;
import com.factory.mapper.InvitationCodeMapper;
import com.factory.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class InvitationCodeServiceImpl extends ServiceImpl<InvitationCodeMapper, InvitationCode> {

    @Autowired
    private UserMapper userMapper;

    @Value("${invite.boss-god-code:}")
    private String bossGodCode;

    private static final SecureRandom random = new SecureRandom();

    public boolean isBossGodCode(String code) {
        return bossGodCode != null && !bossGodCode.isEmpty() && bossGodCode.equals(code);
    }

    @Transactional
    public String generateCode(Long creatorId, String targetRole) {
        User creator = userMapper.selectById(creatorId);
        if (creator == null) throw new BusinessException("创建人不存在");

        String creatorRole = creator.getRole();
        boolean canGenerate = false;
        if ("BOSS".equals(creatorRole) && ("ADMIN".equals(targetRole) || "WAREHOUSE".equals(targetRole) || "OPERATOR".equals(targetRole))) canGenerate = true;
        else if ("ADMIN".equals(creatorRole) && ("WAREHOUSE".equals(targetRole) || "OPERATOR".equals(targetRole))) canGenerate = true;
        else if ("WAREHOUSE".equals(creatorRole) && "OPERATOR".equals(targetRole)) canGenerate = true;
        if (!canGenerate) throw new BusinessException("您没有权限生成该角色的邀请码");

        String code = generateUniqueCode();
        InvitationCode invitation = new InvitationCode();
        invitation.setCode(code);
        invitation.setTargetRole(targetRole);
        invitation.setCreatorId(creatorId);
        invitation.setCreatorRole(creatorRole);
        invitation.setUsed(false);
        invitation.setExpireTime(LocalDateTime.now().plusDays(7));
        this.save(invitation);
        return code;
    }

    @Transactional(rollbackFor = Exception.class)
    public void useCode(String code, Long userId) {
        if (isBossGodCode(code)) {
            User user = userMapper.selectById(userId);
            if (user == null) throw new BusinessException("用户不存在");
            user.setRole("BOSS");
            userMapper.updateById(user);
            return;
        }

        InvitationCode invitation = this.getOne(new LambdaQueryWrapper<InvitationCode>().eq(InvitationCode::getCode, code));
        if (invitation == null) throw new BusinessException("邀请码不存在");
        if (invitation.getUsed()) throw new BusinessException("邀请码已被使用");
        if (invitation.getExpireTime().isBefore(LocalDateTime.now())) throw new BusinessException("邀请码已过期");

        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        int currentLevel = getRoleLevel(user.getRole());
        int targetLevel = getRoleLevel(invitation.getTargetRole());
        if (targetLevel != currentLevel - 1) throw new BusinessException("只能逐级升级");

        user.setRole(invitation.getTargetRole());
        user.setSuperiorId(invitation.getCreatorId());
        userMapper.updateById(user);

        invitation.setUsed(true);
        invitation.setUsedBy(userId);
        if (!this.updateById(invitation)) throw new OptimisticLockingFailureException("邀请码已被他人使用");
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

    private String generateUniqueCode() {
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        if (this.count(new LambdaQueryWrapper<InvitationCode>().eq(InvitationCode::getCode, code)) > 0) {
            return generateUniqueCode();
        }
        return code;
    }

    public List<InvitationCode> getMyGeneratedCodes(Long creatorId) {
        return this.list(new LambdaQueryWrapper<InvitationCode>().eq(InvitationCode::getCreatorId, creatorId).orderByDesc(InvitationCode::getCreateTime));
    }
}