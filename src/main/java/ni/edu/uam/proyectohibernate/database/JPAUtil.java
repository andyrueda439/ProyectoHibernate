package org.example.proyectohibernate.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static EntityManagerFactory entityManagerFactory;

    public static synchronized void iniciar() {
        String password = null;
        if (entityManagerFactory == null) {
            password = System.getenv("DB_password");
            if (password == null || password.isBlank()) {
                throw new RuntimeException("DB_PASSWORD enviorment variable is emtpy");
            }
        }

        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistance.jdbc.password", password);
        entityManagerFactory = Persistence.createEntityManagerFactory("bdbiblioteca", propiedades);

    }
    public static EntityManager getEntityManager(){
        iniciar();
        return entityManagerFactory.createEntityManager();
    }

    public static synchronized void cerrar(){
        if(entityManagerFactory != null && entityManagerFactory.isOpen()){
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
}