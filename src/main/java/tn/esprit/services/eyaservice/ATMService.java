package tn.esprit.services.eyaservice;

import tn.esprit.entities.ATM;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ATMService {

    private Connection cnx = DataBase.getInstance().getCnx();
    private PreparedStatement ps;

    // 🔹 Ajouter un ATM
    public int insert(ATM atm) throws SQLException {
        String query = "INSERT INTO ATM (bankName, status) VALUES (?, ?)";
        ps = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, atm.getBankName());
        ps.setString(2, atm.getStatus());

        int rowsAffected = ps.executeUpdate();
        ResultSet generatedKeys = ps.getGeneratedKeys();

        if (generatedKeys.next()) {
            atm.setId(generatedKeys.getInt(1));
            return atm.getId();
        } else {
            throw new SQLException("Erreur: L'ID de l'ATM n'a pas été généré.");
        }
    }

    // 🔹 Modifier un ATM
    public int update(ATM atm) throws SQLException {
        String query = "UPDATE ATM SET bankName = ?, status = ? WHERE id = ?";
        ps = cnx.prepareStatement(query);
        ps.setString(1, atm.getBankName());
        ps.setString(2, atm.getStatus());
        ps.setInt(3, atm.getId());
        return ps.executeUpdate();
    }

    // 🔹 Supprimer un ATM
    public int delete(int id) throws SQLException {
        String query = "DELETE FROM ATM WHERE id = ?";
        ps = cnx.prepareStatement(query);
        ps.setInt(1, id);
        return ps.executeUpdate();
    }

    // 🔹 Récupérer tous les ATMs
    public List<ATM> showAll() throws SQLException {
        List<ATM> atms = new ArrayList<>();
        String query = "SELECT * FROM ATM";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            ATM atm = new ATM();
            atm.setId(rs.getInt("id"));
            atm.setBankName(rs.getString("bankName"));
            atm.setStatus(rs.getString("status"));
            atms.add(atm);
        }
        return atms;
    }
}