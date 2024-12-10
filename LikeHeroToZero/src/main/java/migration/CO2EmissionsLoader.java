package migration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class CO2EmissionsLoader {
    private static final Logger LOGGER = Logger.getLogger(CO2EmissionsLoader.class.getName());
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emissiondb?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String INSERT_QUERY = "INSERT INTO emissions (country, year, co2_value) VALUES (?, ?, ?)";
    private static final int BATCH_SIZE = 1000;

    public void loadData(String csvFilePath) {
        List<EmissionRecord> records = readCSVFile(csvFilePath);
        if (!records.isEmpty()) {
            insertRecordsToDatabase(records);
        } else {
            LOGGER.warning("No valid records found in CSV file");
        }
    }

    private List<EmissionRecord> readCSVFile(String csvFilePath) {
        List<EmissionRecord> records = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] header = reader.readNext(); // Skip header
            String[] line;
            int lineNumber = 1;

            while ((line = reader.readNext()) != null) {
                lineNumber++;
                try {
                    EmissionRecord record = parseCSVLine(line);
                    if (record != null) {
                        records.add(record);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error parsing line " + lineNumber, e);
                }
            }
        } catch (IOException | CsvException e) {
            LOGGER.log(Level.SEVERE, "Error reading CSV file: " + csvFilePath, e);
        }
        
        return records;
    }

    private EmissionRecord parseCSVLine(String[] values) {
        if (values.length < 4) {
            return null;
        }

        try {
            String country = values[0].trim();
            int year = Integer.parseInt(values[2].trim());
            double co2Value = parseDouble(values[3]);
            
            return new EmissionRecord(country, year, co2Value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void insertRecordsToDatabase(List<EmissionRecord> records) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_QUERY)) {
                int count = 0;
                
                for (EmissionRecord record : records) {
                    pstmt.setString(1, record.getCountry());
                    pstmt.setInt(2, record.getYear());
                    pstmt.setDouble(3, record.getCo2Value());
                    pstmt.addBatch();
                    
                    if (++count % BATCH_SIZE == 0) {
                        pstmt.executeBatch();
                    }
                }
                
                if (count % BATCH_SIZE != 0) {
                    pstmt.executeBatch();
                }
                
                conn.commit();
                LOGGER.info("Successfully inserted " + records.size() + " records");
                
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error during batch insert", e);
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
        }
    }

    private static class EmissionRecord {
        private final String country;
        private final int year;
        private final double co2Value;

        public EmissionRecord(String country, int year, double co2Value) {
            this.country = country;
            this.year = year;
            this.co2Value = co2Value;
        }

        public String getCountry() { return country; }
        public int getYear() { return year; }
        public double getCo2Value() { return co2Value; }
    }

    public static void main(String[] args) {
        CO2EmissionsLoader loader = new CO2EmissionsLoader();
        loader.loadData("co2_emissions_valid_countries.csv");
    }
}