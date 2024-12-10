package models;

import jakarta.persistence.*;

@Entity
@Table(name = "emissions")
public class Emission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;
    private int year;

    @Column(name = "approved")
    private Boolean approved = null; // null = en attente, true = approuvé, false = refusé

    // Ajouter getter et setter


    @Column(name = "co2_value")
    private double co2Value;

    // Getters and Setters

    // ... (same as before)


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getCo2Value() {
        return co2Value;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
    public void setCo2Value(double co2Value) {
        this.co2Value = co2Value;
    }
}

