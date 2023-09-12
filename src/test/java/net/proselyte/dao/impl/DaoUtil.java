package net.proselyte.dao.impl;

import lombok.experimental.UtilityClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Function;

@UtilityClass
class DaoUtil {
    public <T> T execute(SessionFactory sessionFactory, Function<Session, T> function) {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                T entity = function.apply(session);

                transaction.commit();
                return entity;
            } catch (Exception exception) {
                try {
                    transaction.rollback();
                } catch (Exception suppressedException) {
                    exception.addSuppressed(suppressedException);
                }
                throw exception;
            }
        }
    }
}
