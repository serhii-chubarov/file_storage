package net.proselyte.service;

import java.util.List;

public interface CrudService<E> {
    void save(E entity);

    E findById(Integer id);

    List<E> findAll();

    void update(Integer id, E entity);

    void remove(Integer id);
}
