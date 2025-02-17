package tn.esprit.services;

import tn.esprit.entites.User;

import java.sql.SQLException;
import java.util.List;

public interface IService<T>{
    void create(T t) throws SQLException;

    void update(T t) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    T getOneById() throws SQLException;

    User getOneById(int id) throws SQLException;
}
