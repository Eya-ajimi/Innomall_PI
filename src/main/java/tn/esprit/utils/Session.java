package tn.esprit.utils;

import tn.esprit.entities.Utilisateur;

public class Session {
    private static Session instance;
    private Utilisateur currentUser;

    // Constructeur privé pour empêcher l'instanciation directe
    private Session() {}

    // Méthode pour obtenir l'instance unique de Session
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // Getter et Setter pour l'utilisateur connecté
    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Utilisateur currentUser) {
        this.currentUser = currentUser;
    }

    // Méthode pour déconnecter l'utilisateur
    public void logout() {
        this.currentUser = null;
    }

    // Vérifier si un utilisateur est connecté
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}