package net.proselyte.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<E> {
    E save(E entity);

    Optional<E> findById(Integer id);

    List<E> findAll();

    void update(E entity);

    void deleteById(Integer id);
}
