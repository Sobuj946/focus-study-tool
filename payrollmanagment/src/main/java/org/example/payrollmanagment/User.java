package org.example.payrollmanagment;
//package org.example.payrollmanagment.model;

public class User {

    private String username;
    private String password;   // hashed before storage
    private String fullName;
    private String email;

    public User(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }

    public String toFileLine() {
        return username + "|" + password + "|" + fullName + "|" + email;
    }

    public static User fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) {
            return null;
        }
        return new User(parts[0], parts[1], parts[2], parts[3]);
    }
}