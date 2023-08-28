package net.proselyte.dao.impl;

import net.proselyte.dao.AbstractDao;
import net.proselyte.entity.Event;
import net.proselyte.entity.File;
import net.proselyte.entity.User;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

class EventDaoTest extends AbstractDaoTest<Event> {
    private static final Supplier<Event> ENTITY_SUPPLIER = () -> {
        User[] user = new User[1];
        File[] file = new File[1];
        DaoUtil.execute(sessionFactory, session -> {
            UserDao userDao = new UserDao(sessionFactory);
            FileDao fileDao = new FileDao(sessionFactory);
            user[0] = userDao.save(new User("alice"));
            file[0] = fileDao.save(new File("file_1", "/home"));
            return null;
        });

        return new Event(user[0], file[0]);
    };
    private static final Supplier<AbstractDao<Event>> DAO_SUPPLIER = () -> new EventDao(sessionFactory);
    private static final ToIntFunction<Event> ENTITY_TO_INT_FUNCTION = Event::getId;
    private static final Function<Event, Event> ENTITY_TO_UPDATED_ENTITY =
            event -> {
                User[] user = new User[1];
                File[] file = new File[1];
                DaoUtil.execute(sessionFactory, session -> {
                    UserDao userDao = new UserDao(sessionFactory);
                    FileDao fileDao = new FileDao(sessionFactory);
                    user[0] = userDao.save(new User("bob"));
                    file[0] = fileDao.save(new File("file_2", "/home"));
                    return null;
                });

                event.setUser(user[0]);
                event.setFile(file[0]);
                return event;
            };

    public EventDaoTest() {
        super(ENTITY_SUPPLIER, DAO_SUPPLIER, ENTITY_TO_INT_FUNCTION, ENTITY_TO_UPDATED_ENTITY);
    }
}
