package tn.esprit.entites;

public abstract class User {

    protected int id;
    protected String email;
    protected String password;
    protected Role role;

    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(int id) {
        this.id = id;
    }

    public User(int id,String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.id = id;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "user{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
