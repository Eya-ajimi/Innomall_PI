package tn.esprit.tests;

import tn.esprit.entites.Client;
import tn.esprit.entites.Panier;
import tn.esprit.entites.Produit;
import tn.esprit.entites.ShopOwner;
import tn.esprit.services.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class testPanier {
    public static void main(String[] args) {

        Client c1= new Client(1,"houssemjribi@gmzil.com","dftyghujik","houssem", "jribi", 23167213,0,0);
        Client c2= new Client(2);
        Client c3= new Client(3);
        ShopOwner sh1=new ShopOwner(2,"houssemjribi@gmzil.com","dftyghujik","houssem","hello");
        Produit p1 = new Produit(1,2,"sdfghnj",0,25,1818,"gtggtgt");
        Produit p2 = new Produit(1,2,"sdfghnj",0,25,1818,"gtggtgt");
        Produit p3 = new Produit(1,2,"sdfghnj",0,25,1818,"gtggtgt");

      List<Produit> p=new ArrayList<>();
         p.add(p1);
           p.add(p2);
      p.add(p3);
        panierService ps=new panierService();
        CommandeService cs=new CommandeService();

        try {
          ps.ajouterAuPanier(1,3);
//            List<Panier> panier= ps.showAllClientPanier(3);
//            System.out.println(panier.get(0));
//            System.out.println(ps.diminuerQuantite(panier.get(1).getIdCommande(),panier.get(0).getIdProduit()));
            System.out.println(ps.showAllClientPanier(1));
//            System.out.println("------------------");
//            cs.payerCommande(3);
            System.out.println(cs.getCommandeEnCours(1));
            System.out.println(ps.showAllClientPanier(1));


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
}
