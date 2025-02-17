package tn.esprit.tests;

import java.net.URL;

public class Resources {
    public Resources() {
        URL url = getClass().getResource("/fxml/registre.fxml");
        if (url == null) {
            System.out.println("Fichier registre.fxml introuvable !");
        } else {
            System.out.println("Fichier registre.fxml trouvé !");
        }
    }

    public static void main(String[] args) {
        new Resources(); // Exécuter la vérification
    }
}
