package net.proselyte.dao.impl;

import net.proselyte.dao.AbstractDao;
import net.proselyte.entity.User;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

class UserDaoTest extends AbstractDaoTest<User> {
    private static final Supplier<User> ENTITY_SUPPLIER = () -> new User("alice");
    private static final Supplier<AbstractDao<User>> DAO_SUPPLIER = () -> new UserDao(sessionFactory);
    private static final ToIntFunction<User> ENTITY_TO_INT_FUNCTION = User::getId;
    private static final Function<User, User> ENTITY_TO_UPDATED_ENTITY =
            user -> {
                user.setName("bob");
                return user;
            };

    public UserDaoTest() {
        super(ENTITY_SUPPLIER, DAO_SUPPLIER, ENTITY_TO_INT_FUNCTION, ENTITY_TO_UPDATED_ENTITY);
    }
}
