package models;


import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Named("publicEmissionsBean")
@ViewScoped
public class CitizenBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<EmissionsBean.EmissionRecord> emissions;
    private String searchCountry;
    private Integer filterYear;

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
        StringBuilder query = new StringBuilder(
                "SELECT country, year, co2_value FROM emissions WHERE approved = true"
        );

        if (searchCountry != null && !searchCountry.trim().isEmpty()) {
            query.append(" AND country LIKE ?");
        }
        if (filterYear != null) {
            query.append(" AND year = ?");
        }
        query.append(" ORDER BY year DESC, co2_value DESC");

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
                EmissionsBean.EmissionRecord record = new EmissionsBean.EmissionRecord(
                        rs.getString("country"),
                        rs.getInt("year"),
                        rs.getDouble("co2_value")
                );
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

    // Getters and Setters
    public List<EmissionsBean.EmissionRecord> getEmissions() {
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
}