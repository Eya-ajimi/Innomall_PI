package tn.esprit.services;



import java.sql.SQLException;
import java.util.List;

public interface CRUD<T> {
    int insert(T var1) throws SQLException;

    int update(T var1) throws SQLException;

    int delete(T var1) throws SQLException;

    List<T> showAll() throws SQLException;
}
