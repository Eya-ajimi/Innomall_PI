package tn.esprit.services;

import tn.esprit.entities.Event;
import tn.esprit.entities.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public interface CRUD<T>{
    void add(Event event);

    void create(T t) throws SQLException;

    int insert(Utilisateur utilisateur) throws SQLException;

    int update(T t) throws SQLException;

    int delete(Utilisateur utilisateur) throws SQLException;

    List<Utilisateur> showAll() throws SQLException;

    void delete(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    T getOneById() throws SQLException;

    Utilisateur getOneById(int id) throws SQLException;

    Event getById(int id);
}
