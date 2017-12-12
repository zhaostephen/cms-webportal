package com.capitalcrux.cms.dao;

import com.capitalcrux.cms.dao.document.DocumentDao;
import com.capitalcrux.cms.dao.user.UserDao;
import com.capitalcrux.cms.entity.Document;
import com.capitalcrux.cms.entity.Role;
import com.capitalcrux.cms.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

public class DataBaseInitializer
{
    private DocumentDao documentDao;
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    protected DataBaseInitializer()
    {
    }

    public DataBaseInitializer(UserDao userDao, DocumentDao documentDao, PasswordEncoder passwordEncoder)
    {
        this.userDao = userDao;
        this.documentDao = documentDao;
        this.passwordEncoder = passwordEncoder;
    }

    public void initDataBase()
    {
        User userUser = new User("user", this.passwordEncoder.encode("user"));
        userUser.addRole(Role.USER);
        this.userDao.save(userUser);

        User adminUser = new User("admin", this.passwordEncoder.encode("admin"));
        adminUser.addRole(Role.USER);
        adminUser.addRole(Role.ADMIN);
        this.userDao.save(adminUser);

        long timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 24);
        for (int i = 0; i < 10; i++) {
            Document document = new Document();
            document.setContent("This is example content " + i);
            document.setDate(new Date(timestamp));
            this.documentDao.save(document);
            timestamp += 1000 * 60 * 60;
        }
    }
}
