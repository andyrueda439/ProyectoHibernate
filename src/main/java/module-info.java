module ni.edu.uam.proyectohibernate {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    opens ni.edu.uam.proyectohibernate to javafx.fxml;
    opens ni.edu.uam.proyectohibernate.controller to javafx.fxml;
    opens ni.edu.uam.proyectohibernate.model to org.hibernate.orm.core, javafx.base;

    exports ni.edu.uam.proyectohibernate;
    exports ni.edu.uam.proyectohibernate.controller;
    exports ni.edu.uam.proyectohibernate.model;
}