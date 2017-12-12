package com.capitalcrux.cms.dao.accesstoken;

import com.capitalcrux.cms.dao.Dao;
import com.capitalcrux.cms.entity.AccessToken;

public interface AccessTokenDao extends Dao<AccessToken, Long>
{
    AccessToken findByToken(String accessTokenString);
}
