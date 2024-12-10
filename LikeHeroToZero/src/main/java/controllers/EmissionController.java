package controllers;

import jakarta.transaction.Transactional;
import models.Emission;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;

import dao.EmissionDAO;

import java.util.List;
import java.util.logging.Logger;

@Named("emissionController")
@RequestScoped
public class EmissionController {
	private static final Logger logger = Logger.getLogger(EmissionController.class.getName());
    @Inject
    private EntityManager em; // Injected EntityManager instead of creating it directly
    private List<Emission> emissions;

    // Optional: Initialization method if you need to load data immediately
    @PostConstruct
    public void init() {
        loadEmissions();
        System.out.println("Emissions loaded: " + emissions.size());  // Check if data is loaded
        logger.info("EmissionController initialized!");
    }

    
    public void testMethod() {
        System.out.println("Test method called!");
    }

    
    
    public List<Emission> getEmissions() {
        return emissions;
    }

    public void loadEmissions() {
        try {
            emissions = em.createQuery("SELECT e FROM Emission e", Emission.class)
                    .getResultList();
            logger.info("Loaded " + emissions.size() + " emissions");
        } catch (Exception e) {
            logger.severe("Error loading emissions: " + e.getMessage());
        }
    }

    // Add methods to add, update, or delete emissions using the injected EntityManager
   @Transactional
    public void addEmission(Emission emission) {
        em.persist(emission);
        loadEmissions(); // Reload data after adding
    }

    public void updateEmission(Emission emission) {
        em.getTransaction().begin();
        em.merge(emission);
        em.getTransaction().commit();
        loadEmissions(); // Reload data after updating
    }

    @Transactional
    public void deleteEmission(Long id) {
        Emission emission = em.find(Emission.class, id);
        if (emission != null) {
            em.remove(emission);
        }
        loadEmissions(); // Reload data after deletion
    }
}
