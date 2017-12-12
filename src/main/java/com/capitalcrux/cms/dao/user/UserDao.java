package com.capitalcrux.cms.dao.user;

import com.capitalcrux.cms.dao.Dao;
import com.capitalcrux.cms.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDao extends Dao<User, Long>
{
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    User findByName(String name);
}
