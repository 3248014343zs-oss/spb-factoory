package com.factory.security;

import com.factory.entity.User;
import com.factory.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setUserId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        userDetails.setEnabled(user.getStatus() == 1);
        return userDetails;
    }
}