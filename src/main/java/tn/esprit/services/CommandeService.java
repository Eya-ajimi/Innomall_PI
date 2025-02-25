package tn.esprit.services;

import tn.esprit.entites.*;
import tn.esprit.utils.DataBase;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


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

    public void delete(int idCommande) throws SQLException {
        String queryDeleteCommande = "DELETE FROM commande WHERE id = ?";
        try (PreparedStatement psDeleteCommande = cnx.prepareStatement(queryDeleteCommande)) {
            psDeleteCommande.setInt(1, idCommande);
            psDeleteCommande.executeUpdate();
            System.out.println("La commande a été supprimée car le panier est vide.");
        }
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


    /****************************************payement***************************************************/
    //affichage panier payé
    public List<Panier> showAllClientPanierPaye(int commandeId)  {
        List<Panier> paniers = new ArrayList<>();

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

    public int updatestatutPanier(Panier productDansPanier ) throws SQLException {
        String query = "UPDATE panier SET statut = ? WHERE idCommande = ? AND idProduit = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, productDansPanier.getStatut().toString());
            ps.setInt(2, productDansPanier.getIdCommande());
            ps.setInt(3, productDansPanier.getIdProduit());
            return ps.executeUpdate();
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
    public int updateUserBalance(double totalCommande, int idClient) throws SQLException {
        String query = "UPDATE user SET balance = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setDouble(1,totalCommande);
            ps.setInt(2, idClient);
            return ps.executeUpdate();
        }
    }

    //cette fonction est appelé dans le fxml
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
        int updateUserBalanceRow= updateUserBalance(commandeEnCours.getTotal(),idClient);

        if (updateUserBalanceRow > 0) {
            System.out.println("La commande a été payée avec succès.");

            // Mettre à jour le statut de la commande à "payée"
            Commande commandePayee = new Commande(
                    commandeEnCours.getId(),
                    commandeEnCours.getIdClient(),
                    Date.valueOf(LocalDate.now()).toString(),
                    commandeEnCours.getTotal(),
                    StatutCommande.payee // Changer le statut à "payée"
            );

            // Mettre à jour la commande dans la base de données
            int rowsUpdated = update(commandePayee);
            List<Panier> paniers = showAllClientPanierPaye(commandePayee.getId());

            for (Panier panier : paniers) {
                panier.setStatut( StatutCommande.payee);
                updatestatutPanier(panier);
            }

            if (rowsUpdated > 0) {
                System.out.println("statut de commande est modifié avec succé.");

                // Diminuer le stock des produits dans le panier
                Map<Integer, List<Panier>> produitspanierParShop ;
                if (paniers != null) {
                    produitspanierParShop = new HashMap<>();
                    for (Panier panier : paniers) {
                        int idProduit = panier.getIdProduit();
                        int quantite = panier.getQuantite();
                        // Diminuer le stock du produit
                        updateStockProduit(idProduit, quantite);
                        int produitShopId= getProduitById(idProduit).getShopId();

                        if (produitspanierParShop.containsKey(produitShopId)) {
                            // Si oui, ajouter le panier à la liste existante
                            produitspanierParShop.get(produitShopId).add(panier);
                        } else {
                            // Sinon, créer une nouvelle liste et ajouter le panier
                            List<Panier> paniersPourShop = new ArrayList<>();
                            paniersPourShop.add(panier);
                            produitspanierParShop.put(produitShopId, paniersPourShop);
                        }


                    }
                    System.out.println(produitspanierParShop);
                    // Après la boucle for qui remplit produitspanierParShop
                    for (Map.Entry<Integer, List<Panier>> entry : produitspanierParShop.entrySet()) {
                        int shopOwnerId = entry.getKey(); // Récupérer l'ID du shopOwner
                        List<Panier> paniersPourShop = entry.getValue(); // Récupérer la liste des paniers pour ce shopOwner

                        // Récupérer le numéroTicket actuel pour ce shopOwner
                        int numeroTicketActuel = getNumeroTicketForShopOwner(shopOwnerId);

                        System.out.println(numeroTicketActuel);

                        // Incrémenter le numéroTicket
                        int nouveauNumeroTicket = numeroTicketActuel + 1;
                        System.out.println(nouveauNumeroTicket);

                        // Mettre à jour le numéroTicket dans la table user pour ce shopOwner
                        updateNumeroTicketForShopOwner(shopOwnerId, nouveauNumeroTicket);
                        // Mettre à jour le numéroTicket pour chaque panier dans la liste
                        for (Panier panier : paniersPourShop) {
                            updateNumeroTicketInPanier(panier.getIdProduit(),panier.getIdCommande(), nouveauNumeroTicket);
                        }
                    }
                }
            } else {
                System.out.println("modification de statut de commande est échoué.");
            }
        } else {
            System.out.println("Erreur lors de la mise à jour de la commande.");
        }

    }

    //getProduitBYId
    public Produit getProduitById(int idProduit) throws SQLException {

        String query = "SELECT * FROM produit WHERE id = ?";
        try  {
            ps = cnx.prepareStatement(query);
            ps.setInt(1, idProduit);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return new Produit(
                            resultSet.getInt("id"),
                            resultSet.getInt("shopId"),
                            resultSet.getString("description"),
                            resultSet.getInt("promotionId"),
                            resultSet.getInt("stock"),
                            resultSet.getDouble("prix"),
                            resultSet.getString("nom")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public int getNumeroTicketForShopOwner(int shopOwnerId)  {
        String query = "SELECT numeroTicket FROM user WHERE id = ?";
        try  {
            ps = cnx.prepareStatement(query);
            ps.setInt(1, shopOwnerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.getString("numeroTicket")+"hehehehehehe");
                    return rs.getInt("numeroTicket");
                } else {
                    throw new SQLException("ShopOwner non trouvé.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateNumeroTicketForShopOwner(int shopOwnerId, int nouveauNumeroTicket) {
        String query = "UPDATE user SET numeroTicket = ? WHERE id = ?";
        try  {
            ps = cnx.prepareStatement(query);
            ps.setInt(1, nouveauNumeroTicket);
            ps.setInt(2, shopOwnerId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void updateNumeroTicketInPanier(int produitId,int commandePayeeId, int nouveauNumeroTicket)  {
        String query = "UPDATE panier SET numeroTicket = ? WHERE idCommande = ? AND idProduit = ?";
        try  {
            ps = cnx.prepareStatement(query);
            ps.setInt(1, nouveauNumeroTicket);
            ps.setInt(2, commandePayeeId);
            ps.setInt(3, produitId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

/***************************** affichage pour la liste de commande au shopOwner ******************************/

public List<Commande> getCommandesPayeesAujourdhuiPourShopOwner(int shopId) throws SQLException {
    List<Commande> commandes = new ArrayList<>();

    // Récupérer la date d'aujourd'hui au format String
    String dateAujourdhui = Date.valueOf(LocalDate.now()).toString();

    // Requête SQL pour récupérer les commandes payées aujourd'hui
    String queryCommandes = "SELECT * FROM commande WHERE dateCommande = ? AND statut = 'payee'";

    try (PreparedStatement psCommandes = cnx.prepareStatement(queryCommandes)) {
        psCommandes.setString(1, dateAujourdhui);
        try (ResultSet rsCommandes = psCommandes.executeQuery()) {

            // Parcourir les commandes
            while (rsCommandes.next()) {
                Commande commande = new Commande();
                commande.setId(rsCommandes.getInt("id"));
                commande.setIdClient(rsCommandes.getInt("idClient"));
                commande.setDateCommande(rsCommandes.getString("dateCommande"));
                commande.setTotal(rsCommandes.getDouble("total"));
                commande.setStatut(StatutCommande.valueOf(rsCommandes.getString("statut")));
                commande.setPaniers(new ArrayList<>());

                // Récupérer les paniers pour cette commande
                String queryPaniers = "SELECT * FROM panier WHERE idCommande = ? AND statut = 'payee'";
                try (PreparedStatement psPaniers = cnx.prepareStatement(queryPaniers)) {
                    psPaniers.setInt(1, commande.getId());
                    try (ResultSet rsPaniers = psPaniers.executeQuery()) {

                        // Parcourir les paniers
                        while (rsPaniers.next()) {
                            int idProduit = rsPaniers.getInt("idProduit");
                            Produit produit = getProduitById(idProduit);

                            // Vérifier si le produit correspond au shopId
                            if (produit != null && produit.getShopId() == shopId) {
                                Panier panier = new Panier();
                                panier.setIdCommande(rsPaniers.getInt("idCommande"));
                                panier.setNumeroTicket(rsPaniers.getInt("numeroTicket"));
                                panier.setIdProduit(idProduit);
                                panier.setQuantite(rsPaniers.getInt("quantite"));
                                panier.setStatut(StatutCommande.valueOf(rsPaniers.getString("statut")));
                                panier.setNomProduit(produit.getNom());
                                panier.setPrix(panier.getQuantite() * produit.getPrix());

                                // Ajouter le panier à la commande
                                commande.getPaniers().add(panier);
                            }
                        }
                    }
                }

                // Si la commande contient des paniers correspondants, la traiter
                if (!commande.getPaniers().isEmpty()) {
                    // Calculer le total de la commande
                    double totalCommande = commande.getPaniers().stream()
                            .mapToDouble(Panier::getPrix)
                            .sum();
                    commande.setTotal(totalCommande);

                    // Définir le numéro de ticket de la commande
                    commande.setNumeroTicket(commande.getPaniers().get(0).getNumeroTicket());
                    System.out.println("lweeeesssssss"+commande.getPaniers().get(0));
                    String nomClient = getNomClientById(commande.getIdClient());
                    commande.setNomClient(nomClient);


                    // Ajouter la commande à la liste
                    commandes.add(commande);
                }
            }
        }
    }

    return commandes;
}



    public String getNomClientById(int idClient)  {
        String nomClient = null; // Variable pour stocker le nom du client

        // Requête SQL pour récupérer le nom du client
        String query = "SELECT nom FROM user WHERE id = ?";

        try  {
            ps = cnx.prepareStatement(query);
            // Définir le paramètre de la requête (idClient)
            ps.setInt(1, idClient);

            // Exécuter la requête et récupérer le résultat
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Récupérer le nom du client depuis le ResultSet
                    nomClient = rs.getString("nom");
                }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Retourner le nom du client (ou null si aucun client n'est trouvé)
        return nomClient;
    }

    //cette methode dans le filtre par le jour
    public List<Commande> getCommandesPayeesselonJourPourShopOwner(int shopId, String dateDeJour) throws SQLException {
        List<Commande> commandes = new ArrayList<>();



        // Requête SQL pour récupérer les commandes payées aujourd'hui
        String queryCommandes = "SELECT * FROM commande WHERE dateCommande = ? AND statut = 'payee'";

        try (PreparedStatement psCommandes = cnx.prepareStatement(queryCommandes)) {
            psCommandes.setString(1, dateDeJour);
            try (ResultSet rsCommandes = psCommandes.executeQuery()) {

                // Parcourir les commandes
                while (rsCommandes.next()) {
                    Commande commande = new Commande();
                    commande.setId(rsCommandes.getInt("id"));
                    commande.setIdClient(rsCommandes.getInt("idClient"));
                    commande.setDateCommande(rsCommandes.getString("dateCommande"));
                    commande.setTotal(rsCommandes.getDouble("total"));
                    commande.setStatut(StatutCommande.valueOf(rsCommandes.getString("statut")));
                    commande.setPaniers(new ArrayList<>());

                    // Récupérer les paniers pour cette commande
                    String queryPaniers = "SELECT * FROM panier WHERE idCommande = ? AND statut = 'payee'";
                    try (PreparedStatement psPaniers = cnx.prepareStatement(queryPaniers)) {
                        psPaniers.setInt(1, commande.getId());
                        try (ResultSet rsPaniers = psPaniers.executeQuery()) {

                            // Parcourir les paniers
                            while (rsPaniers.next()) {
                                int idProduit = rsPaniers.getInt("idProduit");
                                Produit produit = getProduitById(idProduit);

                                // Vérifier si le produit correspond au shopId
                                if (produit != null && produit.getShopId() == shopId) {
                                    Panier panier = new Panier();
                                    panier.setIdCommande(rsPaniers.getInt("idCommande"));
                                    panier.setNumeroTicket(rsPaniers.getInt("numeroTicket"));
                                    panier.setIdProduit(idProduit);
                                    panier.setQuantite(rsPaniers.getInt("quantite"));
                                    panier.setStatut(StatutCommande.valueOf(rsPaniers.getString("statut")));
                                    panier.setNomProduit(produit.getNom());
                                    panier.setPrix(panier.getQuantite() * produit.getPrix());

                                    // Ajouter le panier à la commande
                                    commande.getPaniers().add(panier);
                                }
                            }
                        }
                    }

                    // Si la commande contient des paniers correspondants, la traiter
                    if (!commande.getPaniers().isEmpty()) {
                        // Calculer le total de la commande
                        double totalCommande = commande.getPaniers().stream()
                                .mapToDouble(Panier::getPrix)
                                .sum();
                        commande.setTotal(totalCommande);

                        // Définir le numéro de ticket de la commande
                        commande.setNumeroTicket(commande.getPaniers().get(0).getNumeroTicket());
                        String nomClient = getNomClientById(commande.getIdClient());
                        commande.setNomClient(nomClient);


                        // Ajouter la commande à la liste
                        commandes.add(commande);
                    }
                }
            }
        }
        Collections.sort(commandes);
        return commandes;
    }

/****************************les methodes pour le changement de statut de commande en recuperer *************************/

public List<Panier> getPaniersByCommandeId(int idCommande) throws SQLException {
    List<Panier> paniers = new ArrayList<>();

    String query = "SELECT * FROM panier WHERE idCommande = ?";
    try (PreparedStatement ps = cnx.prepareStatement(query)) {
        ps.setInt(1, idCommande);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Panier panier = new Panier();
                panier.setIdCommande(rs.getInt("idCommande"));
                panier.setIdProduit(rs.getInt("idProduit"));
                panier.setQuantite(rs.getInt("quantite"));
                panier.setStatut(StatutCommande.valueOf(rs.getString("statut")));

                paniers.add(panier);
            }
        }
    }

    return paniers;
}

    public void updateStatutCommande(int idCommande, StatutCommande statut) throws SQLException {
        String query = "UPDATE commande SET statut = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, statut.toString());
            ps.setInt(2, idCommande);
            ps.executeUpdate();
        }
    }

    public void updateStatutPanier(int idCommande, int idProduit, StatutCommande statut) throws SQLException {
        String query = "UPDATE panier SET statut = ? WHERE idCommande = ? AND idProduit = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, statut.toString());
            ps.setInt(2, idCommande);
            ps.setInt(3, idProduit);
            ps.executeUpdate();
        }
    }

    //cette fonction qui sera appelé dans le controller et les fonctions au dessus sont utilisé dans cette foncion
    public void updateStatutCommandeEtPaniers(int idCommande, int shopId) throws SQLException {
        // Récupérer tous les paniers de la commande
        List<Panier> paniers = getPaniersByCommandeId(idCommande);

        // Variable pour vérifier si tous les paniers sont marqués comme "recuperer"
        boolean tousPaniersRecuperes = true;

        // Parcourir les paniers
        for (Panier panier : paniers) {
            Produit produit = getProduitById(panier.getIdProduit());
            // Vérifier si le produit du panier correspond au shopId
            if (produit.getShopId() == shopId) {
                // Mettre à jour le statut du panier à "recuperer"
                updateStatutPanier(panier.getIdCommande(), panier.getIdProduit(), StatutCommande.recuperer);
            }

            // Vérifier si le panier est encore en statut "payee"
            if (panier.getStatut() == StatutCommande.payee) {
                tousPaniersRecuperes = false;
            }
        }

        // Mettre à jour le statut de la commande si nécessaire
        if (tousPaniersRecuperes) {
            updateStatutCommande(idCommande, StatutCommande.recuperer);
        }
    }



}
