package tn.esprit.entities;

public class ATM {
    private int id;
    private String bankName;
    private String status;

    // Constructors
    public ATM() {
    }

    public ATM(int id, String bankName, String status) {
        this.id = id;
        this.bankName = bankName;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ATM{" +
                "id=" + id +
                ", bankName='" + bankName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
