package application.model.common;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // Role constants
    public static final String ROLE_CEO = "CEO";
    public static final String ROLE_CUSTOMER = "Customer";
    public static final String ROLE_PRODUCTION_MANAGER = "Production Manager";
    public static final String ROLE_ACCOUNTANT = "Accountant";

    private String userId;
    private String username;
    private String password;
    private String role;
    private String fullName;

    public User() {
    }

    public User(String userId, String username, String password, String role, String fullName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    // --- Getters / Setters ---
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}

