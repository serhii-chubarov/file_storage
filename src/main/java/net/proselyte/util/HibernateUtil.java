package net.proselyte.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.entity.Event;
import net.proselyte.entity.File;
import net.proselyte.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

import static java.util.Arrays.asList;
import static org.hibernate.cfg.AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.FORMAT_SQL;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.HIGHLIGHT_SQL;
import static org.hibernate.cfg.AvailableSettings.PASS;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.URL;
import static org.hibernate.cfg.AvailableSettings.USER;

@UtilityClass
@Slf4j
public class HibernateUtil {
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory(String url, String user, String password) {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            Properties settings = new Properties();

            settings.put(DRIVER, org.postgresql.Driver.class.getName());
            settings.put(URL, url);
            settings.put(USER, user);
            settings.put(PASS, password);
            settings.put(DIALECT, org.hibernate.dialect.PostgreSQL10Dialect.class.getName());
            settings.put(SHOW_SQL, "false");
            settings.put(FORMAT_SQL, "true");
            settings.put(HIGHLIGHT_SQL, "false");
            settings.put(HBM2DDL_AUTO, "validate");
            settings.put(CURRENT_SESSION_CONTEXT_CLASS, "thread");
            asList(Event.class, File.class, User.class).forEach(configuration::addAnnotatedClass);

            configuration.setProperties(settings);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            try {
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception exception) {
                log.debug("Exception in HibernateUtil.getSessionFactory {}", exception.getMessage());
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
                throw new HibernateException(exception);
            }
        }
        return sessionFactory;
    }
}
