package net.proselyte.dao.impl;

import net.proselyte.dao.AbstractDao;
import net.proselyte.entity.Event;
import org.hibernate.SessionFactory;

public class EventDao extends AbstractDao<Event> {
    public EventDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
