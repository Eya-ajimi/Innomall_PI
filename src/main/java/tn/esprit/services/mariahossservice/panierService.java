package tn.esprit.services.mariahossservice;

import tn.esprit.entities.*;
import tn.esprit.utils.DataBase;
import tn.esprit.entities.enums.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class panierService implements CRUD<Panier>{

    private final Connection cnx = DataBase.getInstance().getCnx();
    private PreparedStatement ps ;
    private final CommandeService commandeService ;

    public panierService() {
        commandeService = new CommandeService();
    }



    // Méthode pour récupérer un produit par son ID
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


    /*-------------------------------------------------------------------------------------------------------------------------*/

    // Méthode pour vérifier si un produit existe déjà dans le panier(utiliseé dans ajouterAuPanier)
    public boolean produitExisteDansPanier(int idCommande, int idProduit) throws SQLException {
        String query = "SELECT * FROM panier WHERE idCommande = ? AND idProduit = ?";
        ps = cnx.prepareStatement(query);

        ps.setInt(1, idCommande);
        ps.setInt(2, idProduit);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.next();



    }

    /*---------------------------------------------------------*/

    // Méthode pour ajouter un produit au panier
    @Override
    public int insert (Panier newProduct) throws SQLException {
        String query = "INSERT INTO panier (`idCommande` , `idProduit`, `quantite`,`statut`) VALUES (?, ?, ?, ?)";
        ps = cnx.prepareStatement(query);

        ps.setInt(1, newProduct.getIdCommande());
        ps.setInt(2, newProduct.getIdProduit());
        ps.setInt(3, newProduct.getQuantite());
        ps.setString(4, newProduct.getStatut().toString());

        return ps.executeUpdate();

    }

    @Override
    public int update(Panier panier) throws SQLException {
        return 0;
    }

    @Override
    public int delete(Panier panier) throws SQLException {
        return 0;
    }

    /************************************************************************************************************************************/
    public boolean deletePanier(int idCommande, int idProduit) throws SQLException {
        Panier deletedPanier= getPanierByIdCommandeAndIdProduit( idCommande,  idProduit);
        Produit produit= getProduitById( idProduit);
        double prix= produit.getPrix()*deletedPanier.getQuantite();
        // Étape 1 : Supprimer l'article du panier
        String queryDeletePanier = "DELETE FROM panier WHERE idCommande = ? AND idProduit = ?";
        try (PreparedStatement psDeletePanier = cnx.prepareStatement(queryDeletePanier)) {
            psDeletePanier.setInt(1, idCommande);
            psDeletePanier.setInt(2, idProduit);

            int rowsAffected = psDeletePanier.executeUpdate();
            commandeService.updateTotalCommandedown( idCommande,  prix);
            if (rowsAffected > 0) {
                // Étape 2 : Vérifier si la liste des paniers de la commande est vide
                String queryCheckPanier = "SELECT COUNT(*) FROM panier WHERE idCommande = ?";
                try (PreparedStatement psCheckPanier = cnx.prepareStatement(queryCheckPanier)) {
                    psCheckPanier.setInt(1, idCommande);
                    try (ResultSet rs = psCheckPanier.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt(1); // Nombre d'articles restants dans le panier
                            if (count == 0) {
                                // Étape 3 : Supprimer la commande si le panier est vide
                                commandeService.delete(idCommande);
                            }
                        }
                    }
                }
                return true; // Retourne true si l'article a été supprimé
            }
        }
        return false; // Retourne false si aucune ligne n'a été supprimée
    }


    //cette fonction n'est pas utilisable
    @Override
    public List<Panier> showAll() throws SQLException {
        List<Panier> paniers = new ArrayList<>();
        String query = "SELECT p.idCommande, p.idProduit, p.quantite, pr.nom, pr.prix, pr.description " +
                "FROM Panier p " +
                "JOIN Produit pr ON p.idProduit = pr.idProduit";

        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(query)) {


            while (rs.next()) {
                Panier panier = new Panier(
                        rs.getInt("idCommande"),
                        rs.getInt("idProduit"),
                        rs.getInt("quantite"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getString("description")

                );
                panier.setPrix(panier.getPrix()*panier.getQuantite());
                paniers.add(panier);
            }
        }

        return paniers;
    }

//cette fonction qui sera utilisable au lieu de showAll

    public List<Panier> showAllClientPanier(int idClient) throws SQLException {
        List<Panier> paniers = new ArrayList<>();
        Commande cmd = commandeService.getCommandeEnCours(idClient);

        if (cmd != null) {
            int commandeId = cmd.getId();
            String query = "SELECT p.idCommande, p.idProduit, p.quantite, pr.nom, pr.prix, pr.description, pr.stock, pr.image_url " +
                    "FROM panier p " +
                    "JOIN Produit pr ON p.idProduit = pr.id " +
                    "WHERE p.idCommande = ?";

            try (PreparedStatement ps = cnx.prepareStatement(query)) {
                ps.setInt(1, commandeId);
                ResultSet rs = ps.executeQuery();

                // Remplir la liste des paniers
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
                    panier.setImage_url(rs.getString("image_url"));
                    paniers.add(panier);
                }
            }

            // Utiliser un itérateur pour parcourir et modifier la liste
            Iterator<Panier> iterator = paniers.iterator();
            while (iterator.hasNext()) {
                Panier panier = iterator.next();
                int idProduit = panier.getIdProduit();
                Produit produit = getProduitById(idProduit); // Récupérer le produit correspondant

                if (produit != null) {
                    int quantitePanier = panier.getQuantite();
                    int stockProduit = produit.getStock();

                    if (quantitePanier > stockProduit) {
                        if (stockProduit == 0) {
                            // Supprimer l'article du panier si le stock est épuisé
                            deletePanier(panier.getIdCommande(), panier.getIdProduit());
                            iterator.remove(); // Supprimer l'article de la liste de manière sécurisée
                        } else {
                            // Mettre à jour la quantité dans le panier avec le stock disponible
                            updateQuantiteDansPanier(panier.getIdCommande(), panier.getIdProduit(), stockProduit);
                            panier.setQuantite(stockProduit); // Mettre à jour la quantité dans l'objet Panier
                            panier.setPrix(panier.getPrix() / quantitePanier * stockProduit); // Recalculer le prix
                        }
                    }
                }
            }

            return paniers; // Retourner la liste mise à jour
        } else {
            System.out.println("Panier vide");
            return null;
        }
    }


    /*-------------------------------------------------------------------------------------------------------------------*/



    public void ajouterAuPanier(int idClient, int idProduit) throws SQLException{

        try {
            cnx.setAutoCommit(false); // Désactiver l'autocommit pour gérer les transactions

            // Étape 1 : Récupérer le produit par son ID
            Produit produit = getProduitById(idProduit);
            if (produit == null) {
                System.out.println("Produit introuvable.");

            }

            else{
                // Étape 2 : Vérifier le stock du produit
                if (produit.getStock() == 0) {
                    System.out.println("Produit" + produit.getNom() + " hors stock.");

                }

                else{

                    // Étape 3 : Rechercher une commande en cours pour ce client
                    Commande commande = commandeService.getCommandeEnCours(idClient);


                    if (commande != null) {
                        // Étape 4 : Vérifier si le produit existe déjà dans le panier
                        if (produitExisteDansPanier(commande.getId(), idProduit)) {
                            System.out.println("Produit déjà existant dans le panier.");
                            return;
                        }
                        else{
                            // Étape 5 : Ajouter le produit au panier
                            Panier newProduct = new Panier(commande.getId(),idProduit,1,StatutCommande.enCours);
                            insert(newProduct);

                            // Étape 6 : Mettre à jour le total de la commande
                            commandeService.updateTotalCommandeUp(commande.getId(), produit.getPrix());
                        }


                    } else {
                        // Étape 7 : Créer une nouvelle commande
                        commande=new Commande(idClient,Date.valueOf(LocalDate.now()).toString(),StatutCommande.enCours);

                        int commandeId = commandeService.insert(commande);


                        Panier newProduct = new Panier(commandeId,idProduit,1,StatutCommande.enCours);

                        // Étape 8 : Ajouter le produit au panier
                        insert(newProduct);
                        Commande cmd = commandeService.getCommandeEnCours(idClient);

                        // Étape 9 : Mettre à jour le total de la commande
                        commandeService.updateTotalCommandeUp(cmd.getId(), produit.getPrix());
                    }

                    // Étape 10 : Valider la transaction
                    cnx.commit();
                    System.out.println("Produit ajouté au panier avec succès.");

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (cnx != null) {
                try {
                    cnx.rollback(); // Annuler la transaction en cas d'erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (cnx != null) {
                try {
                    cnx.setAutoCommit(true); // Réactiver l'autocommit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }






    /*---------------------------------updateQuantité------------------------------------*/

    public Panier getPanierByIdCommandeAndIdProduit(int idCommande, int idProduit) throws SQLException {
        String queryPanier = "SELECT * FROM panier WHERE idCommande = ? AND idProduit = ?";
        try (PreparedStatement psPanier = cnx.prepareStatement(queryPanier)) {
            psPanier.setInt(1, idCommande);
            psPanier.setInt(2, idProduit);
            try (ResultSet rsPanier = psPanier.executeQuery()) {
                if (rsPanier.next()) {
                    return new Panier(
                            rsPanier.getInt("idCommande"),
                            rsPanier.getInt("idProduit"),
                            rsPanier.getInt("quantite")
                    );
                }
            }
        }
        return null; // Retourne null si aucun panier n'est trouvé
    }

    public int updateQuantiteDansPanier(int idCommande, int idProduit, int newQuantite) throws SQLException {
        String updatePanier = "UPDATE panier SET quantite = ? WHERE idCommande = ? AND idProduit = ?";
        try (PreparedStatement psUpdatePanier = cnx.prepareStatement(updatePanier)) {
            psUpdatePanier.setInt(1, newQuantite);
            psUpdatePanier.setInt(2, idCommande);
            psUpdatePanier.setInt(3, idProduit);
            return psUpdatePanier.executeUpdate();
        }
    }


    /*"c'ette fonction que sera applé dans l'interface "*/
    public int augmenterQuantite(int idCommande, int idProduit) throws SQLException {
        // Récupérer le produit correspondant à idProduit
        Produit pr = getProduitById(idProduit);
        if (pr == null) {
            System.out.println("Produit non trouvé.");
            return -1; // Retourner une valeur invalide pour indiquer une erreur
        }

        // Récupérer l'objet Panier correspondant à idCommande et idProduit
        Panier pn = getPanierByIdCommandeAndIdProduit(idCommande, idProduit);
        if (pn == null) {
            System.out.println("Panier non trouvé pour cette commande et ce produit.");
            return -1; // Retourner une valeur invalide pour indiquer une erreur
        }

        // Calculer la nouvelle quantité
        int newQuantite = pn.getQuantite() + 1;

        // Vérifier si la nouvelle quantité dépasse le stock disponible
        if (newQuantite > pr.getStock()) {
            System.out.println("Indisponible : stock insuffisant.");
            return pn.getQuantite(); // Retourner la quantité actuelle
        }

        // Mettre à jour la quantité dans le panier
        int UpQP= updateQuantiteDansPanier(idCommande, idProduit, newQuantite);
        System.out.println(UpQP +"updateQuantiteDansPanier effectué");
        // Mettre à jour le total dans la table Commande
        int UpQC=commandeService.updateTotalCommandeUp(idCommande, pr.getPrix());
        System.out.println(UpQC +"updateTotalCommande effectué");

        // Retourner la nouvelle quantité
        return newQuantite;
    }

    public int diminuerQuantite(int idCommande, int idProduit) throws SQLException {
        // Récupérer le produit correspondant à idProduit
        Produit pr = getProduitById(idProduit);
        if (pr == null) {
            System.out.println("Produit non trouvé.");
            return -1; // Retourner une valeur invalide pour indiquer une erreur
        }

        // Récupérer l'objet Panier correspondant à idCommande et idProduit
        Panier pn = getPanierByIdCommandeAndIdProduit(idCommande, idProduit);
        if (pn == null) {
            System.out.println("Panier non trouvé pour cette commande et ce produit.");
            return -1; // Retourner une valeur invalide pour indiquer une erreur
        }

        // Calculer la nouvelle quantité
        int newQuantite = pn.getQuantite() - 1;


        // Vérifier si la nouvelle quantité dépasse le stock disponible
        if (newQuantite ==0) {
            System.out.println("Indisponible : you can delete this article.");
            //faire un mise à jour
            return pn.getQuantite(); // Retourner la quantité actuelle
        }

        // Mettre à jour la quantité dans le panier
        int UpQP= updateQuantiteDansPanier(idCommande, idProduit, newQuantite);
        System.out.println(UpQP +"updateQuantiteDansPanier effectué");
        // Mettre à jour le total dans la table Commande
        int UpQC=commandeService.updateTotalCommandedown(idCommande, pr.getPrix());
        System.out.println(UpQC +"updateTotalCommande effectué");

        // Retourner la nouvelle quantité
        return newQuantite;
    }















}