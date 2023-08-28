package net.proselyte.util;

import org.flywaydb.core.Flyway;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.reflect.Field;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HibernateUtilTest {
    @BeforeEach
    public void resetSingleton() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field instance = HibernateUtil.class.getDeclaredField("sessionFactory");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void getSessionFactoryShouldNotThrowException() {
        try (PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")) {
            container.start();
            String url = container.getJdbcUrl();
            String username = container.getUsername();
            String password = container.getPassword();
            Flyway flyway = Flyway.configure()
                                  .dataSource(url, username, password)
                                  .load();
            flyway.migrate();

            assertThatCode(() -> HibernateUtil.getSessionFactory(url, username, password)).doesNotThrowAnyException();
        }
    }

    @Test
    void getSessionFactoryShouldThrowExceptionIfGivenThereIsAnErrorDuringInstantiationSessionFactory() {
        assertThatThrownBy(() -> HibernateUtil.getSessionFactory("error_url", "wrong_username", "wrong_password"))
                .isInstanceOf(HibernateException.class);
    }
}
