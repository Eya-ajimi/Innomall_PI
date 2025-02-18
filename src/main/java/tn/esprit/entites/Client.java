package tn.esprit.entites;

public class Client extends User {

    private String nom;
    private String prenom;
    private int telephone;
    private int nombrePoint;
    private int nombreGains;
    private double balance;

    public Client(String email, String password, String nom, String prenom, int telephone, int nombrePoint, int nombreGains) {
        super(email, password, Role.client);
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.nombrePoint = nombrePoint;
        this.nombreGains = nombreGains;
    }
    public Client(int id, String email, String password, String nom, String prenom, int telephone, int nombrePoint, int nombreGains) {
        super(id,email, password, Role.client);
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.nombrePoint = nombrePoint;
        this.nombreGains = nombreGains;
    }
    public Client(int id) {
        super(id);
    }



    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public int getTelephone() {
        return telephone;
    }

    public int getNombrePoint() {
        return nombrePoint;
    }

    public int getNombreGains() {
        return nombreGains;
    }
}
