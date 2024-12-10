package models;
import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.model.file.UploadedFile;

import java.io.Serializable;
import java.sql.*;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class EmissionsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<EmissionRecord> emissions;

    private UploadedFile uploadedFile;

    private String searchCountry;
    private Integer filterYear;

    // Database connection parameters
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
        loadEmissions();
    }

    public void loadEmissions() {
        emissions = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT country, year, co2_value, approved FROM emissions WHERE 1=1");
        
        if (searchCountry != null && !searchCountry.trim().isEmpty()) {
            query.append(" AND country LIKE ?");
        }
        if (filterYear != null) {
            query.append(" AND year = ?");
        }
        query.append(" ORDER BY co2_value DESC");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            if (searchCountry != null && !searchCountry.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + searchCountry.trim() + "%");
            }
            if (filterYear != null) {
                pstmt.setInt(paramIndex, filterYear);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                EmissionRecord record = new EmissionRecord(
                    rs.getString("country"),
                    rs.getInt("year"),
                    rs.getDouble("co2_value")
                );
                record.setApproved(rs.getBoolean("approved"));
                emissions.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void search() {
        loadEmissions();
    }

    public void clear() {
        searchCountry = null;
        filterYear = null;
        loadEmissions();
    }
    
    private EmissionRecord selectedRecord;

    public void prepareNewRecord() {
        selectedRecord = new EmissionRecord("", 0, 0.0);
    }

    public void deleteRecord(EmissionRecord record) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM emissions WHERE country = ? AND year = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, record.getCountry());
                pstmt.setInt(2, record.getYear());
                pstmt.executeUpdate();
            }
            loadEmissions(); // Refresh the data table
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
 // Modify the setSelectedRecord method to create a new instance to avoid reference issues
    public void setSelectedRecord(EmissionRecord selectedRecord) {
        if (selectedRecord != null) {
            // Create a new instance to avoid reference issues
            this.selectedRecord = new EmissionRecord(
                selectedRecord.getCountry(),
                selectedRecord.getYear(),
                selectedRecord.getCo2Value()
            );
        } else {
            this.selectedRecord = null;
        }
    }

    public void saveRecord() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (selectedRecord == null || 
                selectedRecord.getCountry() == null || 
                selectedRecord.getCountry().trim().isEmpty()) {
                return;
            }

            // Check if we're updating or inserting by looking for existing record
            String checkSql = "SELECT COUNT(*) FROM emissions WHERE country = ? AND year = ?";
            boolean recordExists = false;
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, selectedRecord.getCountry().trim());
                checkStmt.setInt(2, selectedRecord.getYear());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        recordExists = rs.getInt(1) > 0;
                    }
                }
            }

            if (recordExists) {
                // Update existing record
                String updateSql = "UPDATE emissions SET co2_value = ? WHERE country = ? AND year = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, selectedRecord.getCo2Value());
                    updateStmt.setString(2, selectedRecord.getCountry().trim());
                    updateStmt.setInt(3, selectedRecord.getYear());
                    updateStmt.executeUpdate();
                    System.out.println("Record updated successfully"); // Debug log
                }
            } else {
                // Insert new record
                String insertSql = "INSERT INTO emissions (country, year, co2_value) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, selectedRecord.getCountry().trim());
                    insertStmt.setInt(2, selectedRecord.getYear());
                    insertStmt.setDouble(3, selectedRecord.getCo2Value());
                    insertStmt.executeUpdate();
                    System.out.println("New record inserted successfully"); // Debug log
                }
            }
            
            // Reset selected record and reload data
            selectedRecord = null;
            loadEmissions();
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in saveRecord: " + e.getMessage()); // Debug log
        }
    }

    public void editRecord(EmissionRecord record) {
        // Make sure to create a completely new instance
        this.selectedRecord = new EmissionRecord(
            record.getCountry().trim(),
            record.getYear(),
            record.getCo2Value()
        );
        System.out.println("Editing record - Country: " + selectedRecord.getCountry() 
            + ", Year: " + selectedRecord.getYear()
            + ", CO2: " + selectedRecord.getCo2Value()); // Debug log
    }


    public void approveRecord(EmissionRecord record) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE emissions SET approved = true WHERE country = ? AND year = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, record.getCountry());
                pstmt.setInt(2, record.getYear());
                pstmt.executeUpdate();
            }
            loadEmissions(); // Recharger les données après la mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void declineRecord(EmissionRecord record) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE emissions SET approved = false WHERE country = ? AND year = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, record.getCountry());
                pstmt.setInt(2, record.getYear());
                pstmt.executeUpdate();
            }
            loadEmissions(); // Recharger les données après la mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Getter and Setter for selectedRecord
    public EmissionRecord getSelectedRecord() {
        return selectedRecord;
    }

    // Getters and Setters
    public List<EmissionRecord> getEmissions() {
        return emissions;
    }

    public String getSearchCountry() {
        return searchCountry;
    }

    public void setSearchCountry(String searchCountry) {
        this.searchCountry = searchCountry;
    }

    public Integer getFilterYear() {
        return filterYear;
    }

    public void setFilterYear(Integer filterYear) {
        this.filterYear = filterYear;
    }

    // Inner class for emission records
    public static class EmissionRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String country;
        private int year;
        private double co2Value;

        private Boolean approved ;

        public EmissionRecord(String country, int year, double co2Value) {
            this.country = country;
            this.year = year;
            this.co2Value = co2Value;
            this.approved = null ;
        }

        // Getters and Setters
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        public double getCo2Value() { return co2Value; }
        public void setCo2Value(double co2Value) { this.co2Value = co2Value; }

        public Boolean getApproved() {
            return approved;
        }

        public void setApproved(Boolean approved) {
            this.approved = approved;
        }
    }
}