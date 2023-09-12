package net.proselyte.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractDao<E> implements CrudDao<E> {
    private final SessionFactory sessionFactory;
    private final Class<E> entityType = (Class<E>) ((ParameterizedType)
            this.getClass()
                .getGenericSuperclass())
            .getActualTypeArguments()[0];

    protected AbstractDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public E save(E entity) {
        log.debug("Save entity {}", entity);
        Session session = sessionFactory.getCurrentSession();
        session.persist(entity);

        return entity;
    }

    @Override
    public Optional<E> findById(Integer id) {
        log.debug("Find entity by id {}", id);
        Session session = sessionFactory.getCurrentSession();
        E entity = session.find(entityType, id);

        return Optional.ofNullable(entity);
    }

    @Override
    public List<E> findAll() {
        log.debug("Find all entities");
        Session session = sessionFactory.getCurrentSession();
        String entityName = session.getMetamodel()
                                   .entity(entityType)
                                   .getName();
        return session.createQuery("SELECT entity " +
                                           "FROM " + entityName + " AS entity", entityType)
                      .getResultList();
    }

    @Override
    public void update(E entity) {
        log.debug("Update entity {}", entity);
        Session session = sessionFactory.getCurrentSession();
        session.merge(entity);
    }

    @Override
    public void deleteById(Integer id) {
        log.debug("Delete entity by id {}", id);
        Session session = sessionFactory.getCurrentSession();
        E entity = session.find(entityType, id);
        session.remove(entity);
    }
}
