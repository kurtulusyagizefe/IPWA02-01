package dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JpaUtil {
    private static final EntityManagerFactory emf;

    static {
        try {
            System.out.println("Initializing EntityManagerFactory");
            emf = Persistence.createEntityManagerFactory("LikeHeroToZeroPU");
            System.out.println("EntityManagerFactory created successfully");
        } catch (Exception e) {
            System.err.println("Error creating EntityManagerFactory:");
            e.printStackTrace();
            throw new RuntimeException("Failed to create EntityManagerFactory", e);
        }
    }

    @Produces
    @ApplicationScoped
    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }
}