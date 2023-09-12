package net.proselyte.dao.impl;

import net.proselyte.dao.AbstractDao;
import net.proselyte.entity.File;
import org.hibernate.SessionFactory;

public class FileDao extends AbstractDao<File> {
    public FileDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
