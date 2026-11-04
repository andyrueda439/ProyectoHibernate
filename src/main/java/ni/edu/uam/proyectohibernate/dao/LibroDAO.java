package org.example.proyectohibernate.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.proyectohibernate.database.JPAUtil;
import org.example.proyectohibernate.model.Libro;

import java.util.List;

public class LibroDAO {

    public void guardar(Libro libro){
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            //Iniciar la transaccio en PostgreSQL
            tx.begin();
            //Decirle a Hibernate que prepare la persistencia del objeto
            em.persist(libro);
            tx.commit();
        }catch (RuntimeException ex){
            if(tx.isActive()){
                tx.rollback();
            }
            throw ex;
        }finally {
            em.close();
        }
    }

    public List<Libro> listar(){
        EntityManager em = JPAUtil.getEntityManager();
        try{
            return em.createQuery("SELECT l FROM Libro l ORDER BY L.id", Libro.class).getResultList();
        }
        finally {
            em.close();
        }
    }
}