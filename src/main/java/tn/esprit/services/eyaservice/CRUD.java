package tn.esprit.services.eyaservice;

import tn.esprit.entities.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public interface CRUD<T> {

    void create(Utilisateur user) throws SQLException;

    int insert(T t) throws SQLException;
    int update(T t) throws SQLException;
    int delete(T t) throws SQLException;
    List<T> showAll() throws SQLException;

    void delete(int id) throws SQLException;

    List<Utilisateur> getAll() throws SQLException;

    Utilisateur getOneById() throws SQLException;

    Utilisateur getOneById(int id) throws SQLException;
}