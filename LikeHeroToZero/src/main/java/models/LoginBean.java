package models;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.*;

@Named
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private User currentUser;
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emissiondb?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Constructor
    public LoginBean() {
        // Add a debug message
        System.out.println("LoginBean constructed");
    }

    public String login() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT u.*, r.role_name FROM users u " +
                           "JOIN roles r ON u.role_id = r.id " +
                           "WHERE u.username = ? AND u.password = ? AND u.active = true";
                           
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            // Create user object
                            currentUser = new User();
                            currentUser.setId(rs.getInt("id"));
                            currentUser.setUsername(rs.getString("username"));
                            currentUser.setFullName(rs.getString("full_name"));
                            currentUser.setEmail(rs.getString("email"));
                            currentUser.setRoleId(rs.getInt("role_id"));
                            currentUser.setRoleName(rs.getString("role_name"));
                            
                            // Update last login time
                            updateLastLogin(conn, username);
                            
                            // Store user in session
                            FacesContext.getCurrentInstance().getExternalContext()
                                .getSessionMap().put("user", currentUser);
                            
                            // Redirect based on role
                            return getHomePageByRole(currentUser.getRoleName());
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, 
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                "Invalid credentials", "Please try again"));
                            return null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "System Error", "Please try again later"));
            e.printStackTrace();
            return null;
        }
    }
    
    private String getHomePageByRole(String role) {
        switch (role) {
            case "PUBLISHER":
                return "/publisher/dashboard?faces-redirect=true";
            case "SCIENTIST":
                return "/scientists/dashboard?faces-redirect=true";
            case "CITIZEN":
                return "/user/emissions?faces-redirect=true";
            default:
                return "/user/emissions?faces-redirect=true";
        }
    }
    
    private void updateLastLogin(Connection conn, String username) throws SQLException {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login?faces-redirect=true";
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}