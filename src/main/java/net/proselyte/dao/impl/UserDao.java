package net.proselyte.dao.impl;

import net.proselyte.dao.AbstractDao;
import net.proselyte.entity.User;
import org.hibernate.SessionFactory;

public class UserDao extends AbstractDao<User> {
    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
