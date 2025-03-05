package tn.esprit.services.mariahossservice;

import tn.esprit.entities.Commande;
import tn.esprit.entities.Utilisateur;
import tn.esprit.entities.enums.Role;
import tn.esprit.entities.enums.StatutCommande;
import tn.esprit.utils.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class commandeShopownerService {
    private final Connection cnx = DataBase.getInstance().getCnx();
    private PreparedStatement ps ;


    public List<Utilisateur> getShopOwners() {
        List<Utilisateur> shopOwners = new ArrayList<>();
        String query = "SELECT id, numeroTicket FROM user WHERE role = ?";

        try  {
            ps = cnx.prepareStatement(query);
            ps.setString(1, Role.SHOPOWNER.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                shopOwners.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getInt("numeroTicket")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la sélection : " + e.getMessage());
        }

        return shopOwners;
    }

    public void updateShopOwnerTicket( int id) {
        String query = "UPDATE user SET numeroTicket = 0 WHERE id = ?";

        try {
            ps= cnx.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }





}