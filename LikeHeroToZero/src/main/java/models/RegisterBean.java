package models;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class RegisterBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String fullName;
    private String email;
    private Integer selectedRoleId;
    private List<SelectItem> availableRoles;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/emissiondb?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() {
        loadRoles();
    }

    private void loadRoles() {
        availableRoles = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, role_name FROM roles";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    availableRoles.add(new SelectItem(
                            rs.getInt("id"),
                            rs.getString("role_name")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String register() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Vérifier si l'utilisateur existe déjà
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Username already exists", null));
                    return null;
                }
            }

            // Récupérer le role_name
            String roleName = null;
            String getRoleSql = "SELECT role_name FROM roles WHERE id = ?";
            try (PreparedStatement roleStmt = conn.prepareStatement(getRoleSql)) {
                roleStmt.setInt(1, selectedRoleId);
                ResultSet rs = roleStmt.executeQuery();
                if (rs.next()) {
                    roleName = rs.getString("role_name");
                }
            }

            // Insérer le nouvel utilisateur avec le role_name
            String insertSql = "INSERT INTO users (username, password, full_name, email, role_id, role_name, created_date, active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, true)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, fullName);
                pstmt.setString(4, email);
                pstmt.setInt(5, selectedRoleId);
                pstmt.setString(6, roleName);
                pstmt.executeUpdate();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Registration successful", "You can now login"));
                return "/login?faces-redirect=true";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error during registration", null));
            return null;
        }
    }
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getSelectedRoleId() { return selectedRoleId; }
    public void setSelectedRoleId(Integer selectedRoleId) { this.selectedRoleId = selectedRoleId; }

    public List<SelectItem> getAvailableRoles() { return availableRoles; }
}