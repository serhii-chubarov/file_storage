package net.proselyte.dao.impl;

import net.proselyte.dao.AbstractDao;
import net.proselyte.entity.File;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

class FileDaoTest extends AbstractDaoTest<File> {
    private static final Supplier<File> ENTITY_SUPPLIER = () -> new File("file_1", "/home");
    private static final Supplier<AbstractDao<File>> DAO_SUPPLIER = () -> new FileDao(sessionFactory);
    private static final ToIntFunction<File> ENTITY_TO_INT_FUNCTION = File::getId;
    private static final Function<File, File> ENTITY_TO_UPDATED_ENTITY =
            file -> {
                file.setName("file_2");
                return file;
            };

    public FileDaoTest() {
        super(ENTITY_SUPPLIER, DAO_SUPPLIER, ENTITY_TO_INT_FUNCTION, ENTITY_TO_UPDATED_ENTITY);
    }
}
