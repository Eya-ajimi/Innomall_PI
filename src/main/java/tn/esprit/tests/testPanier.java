package tn.esprit.tests;

import tn.esprit.entites.Client;
import tn.esprit.entites.Panier;
import tn.esprit.entites.Produit;
import tn.esprit.entites.ShopOwner;
import tn.esprit.services.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class testPanier {
    public static void main(String[] args) {
        // Initialisation des objets
        Client c1 = new Client(1, "houssemjribi@gmzil.com", "dftyghujik", "houssem", "jribi", 23167213, 0, 0);
        Client c2 = new Client(2);
        Client c3 = new Client(3);
        ShopOwner sh1 = new ShopOwner(2, "houssemjribi@gmzil.com", "dftyghujik", "houssem", "hello");
        Produit p1 = new Produit(1, 2, "sdfghnj", 0, 25, 1818, "gtggtgt");
        Produit p2 = new Produit(1, 2, "sdfghnj", 0, 25, 1818, "gtggtgt");
        Produit p3 = new Produit(1, 2, "sdfghnj", 0, 25, 1818, "gtggtgt");

        List<Produit> p = new ArrayList<>();
        p.add(p1);
        p.add(p2);
        p.add(p3);

        panierService ps = new panierService();
        CommandeService cs = new CommandeService();
/***********************************************************************************************************************CroneMethode***************************************************************/
        try {
            // Configuration du scheduler
//            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//            commandeShopownerService cmdShop = new commandeShopownerService();

//            scheduler.scheduleAtFixedRate(() -> {
//                List<ShopOwner> shopOwners = cmdShop.getShopOwners();
//                for (ShopOwner shopOwner : shopOwners) {
//                    cmdShop.updateShopOwnerTicket(shopOwner.getId());
//                    System.out.println("Mise à jour du shopOwner ID " + shopOwner.getId() + " : numeroTicket = 0");
//                }
//            }, 0, 24, TimeUnit.HOURS);

            // Opérations sur le panier
            ps.ajouterAuPanier(1, 1);
            ps.ajouterAuPanier(1, 2);
            ps.ajouterAuPanier(1, 3);
            ps.ajouterAuPanier(1, 4);
            ps.ajouterAuPanier(1, 5);


            // Affichage des résultats
            System.out.println(cs.getCommandeEnCours(1));
            System.out.println(ps.showAllClientPanier(1));
            System.out.println("------------------------------------");
            cs.payerCommande(1);
            System.out.println(cs.getCommandesPayeesAujourdhuiPourShopOwner(3));
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("Erreur générale : " + ex.getMessage());
        }
    }
    /***********************************************************************************************************************CroneMethode***************************************************************/

}