package tn.esprit.tests;

import tn.esprit.services.*;
import java.sql.SQLException;
import tn.esprit.entities.*;
import tn.esprit.services.mariahossservice.CommandeService;
import tn.esprit.services.mariahossservice.panierService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class testpanier {
    public static void main(String[] args) {
//        // Initialisation des objets
//        Client c1 = new Client(1, "houssemjribi@gmzil.com", "dftyghujik", "houssem", "jribi", 23167213, 0, 0);
//        Client c2 = new Client(2);
//        Client c3 = new Client(3);
//        ShopOwner sh1 = new ShopOwner(2, "houssemjribi@gmzil.com", "dftyghujik", "houssem", "hello");
//        Produit p1 = new Produit(1, 2, "sdfghnj", 0, 25, 1818, "gtggtgt");
//        Produit p2 = new Produit(1, 2, "sdfghnj", 0, 25, 1818, "gtggtgt");
//        Produit p3 = new Produit(1, 2, "sdfghnj", 0, 25, 1818, "gtggtgt");
//
//        List<Produit> p = new ArrayList<>();
//        p.add(p1);
//        p.add(p2);
//        p.add(p3);

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
            ps.ajouterAuPanier(9, 11);
            ps.ajouterAuPanier(9, 12);
            ps.ajouterAuPanier(9, 18);
            ps.ajouterAuPanier(9, 20);
            ps.ajouterAuPanier(9, 23);
            ps.ajouterAuPanier(9, 25);
            ps.ajouterAuPanier(9, 26);
            ps.ajouterAuPanier(9, 29);


            // Affichage des résultats
            System.out.println(cs.getCommandeEnCours(6));
            System.out.println(ps.showAllClientPanier(6));
            System.out.println("-----------------------------------------------------------------------------------");
//            cs.payerCommande(9);
            System.out.println(cs.getCommandesPayeesAujourdhuiPourShopOwner(1));
//            cs.updateStatutCommandeEtPaniers(33,3);
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("Erreur générale : " + ex.getMessage());
        }
    }
    /***********************************************************************************************************************CroneMethode***************************************************************/

}
