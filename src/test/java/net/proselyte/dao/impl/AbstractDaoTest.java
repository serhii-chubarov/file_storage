package net.proselyte.dao.impl;

import net.proselyte.dao.AbstractDao;
import net.proselyte.util.HibernateUtil;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;

abstract class AbstractDaoTest<T> {
    private static final PostgresContainer CONTAINER = PostgresContainer.getTestContainerInstance();

    private final Supplier<T> entitySupplier;
    private final ToIntFunction<T> entityToId;
    private final Function<T, T> entityToUpdatedEntity;
    private final Supplier<AbstractDao<T>> daoSupplier;
    private final Class<T> entityType = (Class<T>) ((ParameterizedType)
            this.getClass()
                .getGenericSuperclass())
            .getActualTypeArguments()[0];

    protected static SessionFactory sessionFactory;

    public AbstractDaoTest(Supplier<T> entitySupplier, Supplier<AbstractDao<T>> daoSupplier,
                           ToIntFunction<T> entityToId, Function<T, T> entityToUpdatedEntity) {
        this.entitySupplier = entitySupplier;
        this.daoSupplier = daoSupplier;
        this.entityToId = entityToId;
        this.entityToUpdatedEntity = entityToUpdatedEntity;
    }

    @BeforeAll
    static void beforeAll() {
        CONTAINER.start();
        String url = CONTAINER.getJdbcUrl();
        String username = CONTAINER.getUsername();
        String password = CONTAINER.getPassword();

        Flyway flyway = Flyway.configure()
                              .dataSource(url, username, password)
                              .load();
        flyway.migrate();
        sessionFactory = HibernateUtil.getSessionFactory(url, username, password);
    }

    @BeforeEach
    void beforeEach() {
        doInHibernate(() -> sessionFactory, (Session session) -> {
            String entityName = session.getMetamodel()
                                       .entity(entityType)
                                       .getName();
            session.createQuery("DELETE FROM " + entityName)
                   .executeUpdate();
        });
    }

    @Test
    void saveShouldSaveEntity() {
        T expectedEntity = entitySupplier.get();
        T entity = DaoUtil.execute(sessionFactory, session -> daoSupplier.get().save(expectedEntity));
        int id = entityToId.applyAsInt(entity);
        T actualEntity = doInHibernate(() -> sessionFactory, (Session session) -> session.find(entityType, id));

        assertThat(actualEntity).isEqualTo(expectedEntity);
    }

    @Test
    void findByIdShouldReturnEntity() {
        T expectedEntity = entitySupplier.get();
        T entity = DaoUtil.execute(sessionFactory, session -> daoSupplier.get().save(expectedEntity));
        int id = entityToId.applyAsInt(entity);
        Optional<T> actualEntity = DaoUtil.execute(sessionFactory, session -> daoSupplier.get().findById(id));

        assertThat(actualEntity).contains(expectedEntity);
    }

    @Test
    void findAllShouldReturnListOfEntities() {
        List<T> expectedEntities = asList(entitySupplier.get(), entitySupplier.get(),
                                          entitySupplier.get(), entitySupplier.get());
        DaoUtil.execute(sessionFactory, session -> {
            expectedEntities.forEach(daoSupplier.get()::save);
            return null;
        });

        List<T> entities = DaoUtil.execute(sessionFactory, session -> daoSupplier.get().findAll());
        assertThat(entities).isEqualTo(expectedEntities);
    }

    @Test
    void updateShouldUpdateEntity() {
        T initialEntity = entitySupplier.get();
        T entity = DaoUtil.execute(sessionFactory, session -> daoSupplier.get().save(initialEntity));
        T expectedEntity = entityToUpdatedEntity.apply(entity);
        DaoUtil.execute(sessionFactory, session -> {
            daoSupplier.get().update(expectedEntity);
            return null;
        });
        int id = entityToId.applyAsInt(entity);
        T actualEntity = doInHibernate(() -> sessionFactory, (Session session) -> session.find(entityType, id));

        assertThat(actualEntity).isEqualTo(expectedEntity);
    }

    @Test
    void deleteByIdShouldDeleteEntity() {
        T expectedEntity = entitySupplier.get();
        T entity = DaoUtil.execute(sessionFactory, session -> daoSupplier.get().save(expectedEntity));
        int id = entityToId.applyAsInt(entity);
        DaoUtil.execute(sessionFactory, session -> {
            daoSupplier.get().deleteById(id);
            return null;
        });
        T actualEntity = doInHibernate(() -> sessionFactory, (Session session) -> session.find(entityType, id));

        assertThat(actualEntity).isNull();
    }
}
