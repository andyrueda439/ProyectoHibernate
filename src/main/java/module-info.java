module ni.edu.uam.proyectohibernate {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.proyectohibernate to javafx.fxml;
    exports ni.edu.uam.proyectohibernate;
}