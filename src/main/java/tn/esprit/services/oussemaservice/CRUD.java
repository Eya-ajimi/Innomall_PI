package tn.esprit.services.oussemaservice;

import java.util.List;
import tn.esprit.entities.Event;

public interface CRUD<T> {
    void add(T t);
    void update(T t);
    void delete(int id);
    List<T> getAll();
    T getById(int id);
}