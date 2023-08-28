package net.proselyte.context;

import net.proselyte.dao.DatabaseSettings;
import net.proselyte.dao.impl.EventDao;
import net.proselyte.dao.impl.FileDao;
import net.proselyte.dao.impl.UserDao;
import net.proselyte.dto.mapper.EventMapper;
import net.proselyte.dto.mapper.FileMapper;
import net.proselyte.dto.mapper.UserMapper;
import net.proselyte.service.EventService;
import net.proselyte.service.FileService;
import net.proselyte.service.UserService;
import net.proselyte.service.impl.EventServiceImpl;
import net.proselyte.service.impl.FileServiceImpl;
import net.proselyte.service.impl.UserServiceImpl;
import net.proselyte.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class ApplicationContextInjector {
    private static final EventService EVENT_SERVICE = new EventServiceImpl(EventMapper.INSTANCE,
                                                                           UserMapper.INSTANCE,
                                                                           FileMapper.INSTANCE,
                                                                           new EventDao(getSessionFactory()));

    private static final UserService USER_SERVICE = new UserServiceImpl(UserMapper.INSTANCE,
                                                                        new UserDao(getSessionFactory()));

    private static final FileService FILE_SERVICE = new FileServiceImpl(FileMapper.INSTANCE,
                                                                        new FileDao(getSessionFactory()),
                                                                        USER_SERVICE,
                                                                        EVENT_SERVICE);

    private static ApplicationContextInjector injector;

    private ApplicationContextInjector() {
    }

    public static ApplicationContextInjector getInstance() {
        if (injector == null) {
            injector = new ApplicationContextInjector();
        }

        return injector;
    }

    public EventService getEventServiceInstance() {
        return EVENT_SERVICE;
    }

    public FileService getFileServiceInstance() {
        return FILE_SERVICE;
    }

    public UserService getUserServiceInstance() {
        return USER_SERVICE;
    }

    public static SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory(
                DatabaseSettings.getUrl(),
                DatabaseSettings.getUser(),
                DatabaseSettings.getPassword());
    }
}
