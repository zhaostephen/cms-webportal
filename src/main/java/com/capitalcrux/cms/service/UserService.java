package com.capitalcrux.cms.service;

import com.capitalcrux.cms.entity.AccessToken;
import com.capitalcrux.cms.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService
{
    User findUserByAccessToken(String accessToken);

    AccessToken createAccessToken(User user);
}
