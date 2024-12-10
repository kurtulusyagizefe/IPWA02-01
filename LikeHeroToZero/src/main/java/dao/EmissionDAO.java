package dao;

import models.Emission;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class EmissionDAO {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("LikeHeroToZeroPU");
    
    

    public List<Emission> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Emission> emissions = null;
        try {
            TypedQuery<Emission> query = em.createQuery("SELECT e FROM Emission e", Emission.class);
            emissions = query.getResultList();
        } finally {
            em.close();
        }
        System.out.println("Data retrieved: " + findAll().size() + " records");

        return emissions;
    }
    

    public List<Emission> findByCountry(String country) {
        EntityManager em = emf.createEntityManager();
        List<Emission> emissions = null;
        try {
            TypedQuery<Emission> query = em.createQuery("SELECT e FROM Emission e WHERE e.country = :country", Emission.class);
            query.setParameter("country", country);
            emissions = query.getResultList();
        } finally {
            em.close();
        }
        return emissions;
    }

    public void save(Emission emission) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(emission);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

