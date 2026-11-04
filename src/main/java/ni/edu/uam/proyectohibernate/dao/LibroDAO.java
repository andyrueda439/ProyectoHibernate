package ni.edu.uam.proyectohibernate.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ni.edu.uam.proyectohibernate.database.JPAUtil;
import ni.edu.uam.proyectohibernate.model.Libro;

import java.util.List;

public class LibroDAO {

    public void guardar(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(libro);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Libro> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Libro l ORDER BY l.id", Libro.class).getResultList();
        } finally {
            em.close();
        }
    }
}