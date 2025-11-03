package com.backend.trafico.service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public abstract class BaseService<T, ID> {
    protected final JpaRepository<T, ID> repository;

    protected BaseService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    public List<T> findAll() { return repository.findAll(); }
    public T findById(ID id) { return repository.findById(id).orElse(null); }
    public T save(T entity) { return repository.save(entity); }
    public T update(ID id, T entity) { return repository.save(entity); }
    public void delete(ID id) { repository.deleteById(id); }
}
