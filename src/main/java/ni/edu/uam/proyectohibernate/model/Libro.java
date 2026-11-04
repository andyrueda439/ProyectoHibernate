package org.example.proyectohibernate.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "libro", schema = "catalogos")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "autor", nullable = false, length = 100)
    private String autor;

    @Column(name = "anio_publicacion", nullable = false)
    private String anio_publicacion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

}