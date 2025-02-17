package tn.esprit.services;

import tn.esprit.entites.*;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CommandeService implements CRUD<Commande> {

    private final Connection cnx = DataBase.getInstance().getCnx();
    private PreparedStatement ps ;



    @Override
    public int insert(Commande cmd) throws SQLException {

        String query = "INSERT INTO commande (`idClient`, `dateCommande`, `statut`, `total`) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, cmd.getIdClient());
            statement.setString(2, cmd.getDateCommande());
            statement.setString(3, cmd.getStatut().toString());
            statement.setDouble(4, 0.0);
            statement.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return id;
                }
            }
        }
        return -1;
    }

//rendre le statut confirme
    @Override
    public int update(Commande commande) throws SQLException {
    String query = "UPDATE Commande SET statut = ? WHERE id = ?";
    try (PreparedStatement ps = cnx.prepareStatement(query)) {
        ps.setString(1, commande.getStatut().toString());
        ps.setInt(2, commande.getId());
        return ps.executeUpdate();
        }
    }

    public int updateTotalCommandeUp(int idCommande, double prix) throws SQLException {
        String query = "UPDATE commande SET total = total + ? WHERE id = ?";
        ps = cnx.prepareStatement(query);

        ps.setDouble(1, prix);
        ps.setInt(2, idCommande);

        return ps.executeUpdate();

    }
    public int updateTotalCommandedown(int idCommande, double prix) throws SQLException {
        String query = "UPDATE commande SET total = total - ? WHERE id = ?";
        ps = cnx.prepareStatement(query);

        ps.setDouble(1, prix);
        ps.setInt(2, idCommande);

        return ps.executeUpdate();

    }





    @Override
    public int delete(Commande commande) throws SQLException {
        return 0;
    }

    @Override
    public List<Commande> showAll() throws SQLException {
        return List.of();
    }

    // Méthode pour récupérer une commande en cours pour un client
    public Commande getCommandeEnCours(int idClient) throws SQLException {
        String query = "SELECT * FROM commande WHERE idClient = ? AND statut = ?";
        try {
            ps = cnx.prepareStatement(query);
            ps.setInt(1, idClient);
            ps.setString(2, StatutCommande.enCours.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {

                    return new Commande(
                            resultSet.getInt("id"),
                            resultSet.getInt("idClient"),
                            resultSet.getString("dateCommande"),
                            resultSet.getDouble("total"),
                            StatutCommande.valueOf(resultSet.getString("statut"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }



    public Commande getCommandeConfirme(int idClient) throws SQLException {
        String query = "SELECT * FROM commande WHERE idClient = ? AND statut = ?";
        try {
            ps = cnx.prepareStatement(query);
            ps.setInt(1, idClient);
            ps.setString(2, StatutCommande.confirme.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return new Commande(
                            resultSet.getInt("id"),
                            resultSet.getInt("idClient"),
                            resultSet.getString("dateCommande"),
                            resultSet.getDouble("total"),
                            StatutCommande.valueOf(resultSet.getString("statut"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

/****************************************payement************************************************/
    //affichage panier pour payment
    public List<Panier> showAllClientPanierForPayement(int idClient) throws SQLException {
        List<Panier> paniers = new ArrayList<>();
        Commande cmd= getCommandeEnCours(idClient);
        if(cmd!= null) {
            int commandeId = cmd.getId();
            String query = "SELECT p.idCommande, p.idProduit, p.quantite, pr.nom, pr.prix, pr.description " +
                    "FROM panier p " +
                    "JOIN Produit pr ON p.idProduit = pr.id " +
                    "WHERE p.idCommande = ?";


            try  {
                ps = cnx.prepareStatement(query);
                ps.setInt(1, commandeId);
                ResultSet rs = ps.executeQuery();


                while (rs.next()) {
                    Panier panier = new Panier(
                            rs.getInt("idCommande"),
                            rs.getInt("idProduit"),
                            rs.getInt("quantite"),
                            rs.getString("nom"),
                            rs.getDouble("prix"),
                            rs.getString("description")

                    );
                    panier.setPrix(panier.getPrix() * panier.getQuantite());
                    paniers.add(panier);



                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return paniers;


        }
        else {
            System.out.println("panier vide");
            return null;
        }
    }

    public void updateStockProduit(int idProduit, int quantiteVendue) throws SQLException {
        String query = "UPDATE Produit SET stock = stock - ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, quantiteVendue);
            ps.setInt(2, idProduit);
            ps.executeUpdate();
        }
    }
    public double getBalanceClient(int idClient) throws SQLException {
        String query = "SELECT balance FROM user WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, idClient);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    System.out.println("Solde du client récupéré : " + balance); // Log pour débogage
                    return balance;
                }
            }
        }
        return -1; // Retourne -1 si le client n'est pas trouvé
    }


    public void payerCommande(int idClient) throws SQLException {
        // Récupérer le solde du client
        double balanceClient = getBalanceClient(idClient);
        if (balanceClient < 0) {
            System.out.println("Client non trouvé ou solde indisponible.");
            return;
        }

        // Récupérer la commande en cours du client
        Commande commandeEnCours = getCommandeEnCours(idClient);
        if (commandeEnCours == null) {
            System.out.println("Aucune commande en cours trouvée pour ce client.");
            return;
        }

        double totalCommande = commandeEnCours.getTotal();

        System.out.println("Solde du client : " + balanceClient);
        System.out.println("Total de la commande : " + totalCommande);

        // Comparer le total de la commande avec le solde du client
        if (totalCommande > balanceClient) {
            System.out.println("Votre solde est insuffisant pour payer cette commande. Solde : " + balanceClient);
            return;
        }


        // Mettre à jour le statut de la commande à "payée"
        Commande commandePayee = new Commande(
                commandeEnCours.getId(),
                commandeEnCours.getIdClient(),
                commandeEnCours.getDateCommande(),
                commandeEnCours.getTotal(),
                StatutCommande.confirme // Changer le statut à "payée"
        );

        // Mettre à jour la commande dans la base de données
        int rowsUpdated = update(commandePayee);
        if (rowsUpdated > 0) {
            System.out.println("La commande a été payée avec succès.");

            // Diminuer le stock des produits dans le panier
            List<Panier> paniers = showAllClientPanierForPayement(idClient);
            if (paniers != null) {
                for (Panier panier : paniers) {
                    int idProduit = panier.getIdProduit();
                    int quantite = panier.getQuantite();

                    // Diminuer le stock du produit
                    updateStockProduit(idProduit, quantite);
                }
            }
        } else {
            System.out.println("Erreur lors de la mise à jour de la commande.");
        }
    }


}
