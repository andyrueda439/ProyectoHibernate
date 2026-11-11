package ni.edu.uam.proyectohibernate.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.proyectohibernate.dao.CategoriaDAO;
import ni.edu.uam.proyectohibernate.dao.LibroDAO;
import ni.edu.uam.proyectohibernate.model.Categoria;
import ni.edu.uam.proyectohibernate.model.Libro;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class LibroController {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtAnio;
    @FXML private TextField txtPrecio;
    @FXML private ComboBox<Categoria> cmbCategoria;

    @FXML private Button btnGuardar;
    @FXML private Button btnCargar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;

    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, Long> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, Integer> colAnio;
    @FXML private TableColumn<Libro, BigDecimal> colPrecio;
    @FXML private TableColumn<Libro, String> colCategoria;

    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private Long idSeleccionado = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        colCategoria.setCellValueFactory(data -> {
            Categoria categoria = data.getValue().getCategoria();
            String nombre = categoria != null ? categoria.getNombre() : "Sin categoria";
            return new SimpleStringProperty(nombre);
        });

        tblLibros.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null) {
                seleccionarLibro(nuevo);
            } else {
                idSeleccionado = null;
                actualizarBotones();
            }
        });

        cargarCategorias();
        cargar();
        actualizarBotones();
    }

    private void cargarCategorias() {
        try {
            List<Categoria> categorias = categoriaDAO.listar();
            cmbCategoria.setItems(FXCollections.observableArrayList(categorias));
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudieron cargar las categorías: " + ex.getMessage());
        }
    }

    private void seleccionarLibro(Libro libro) {
        idSeleccionado = libro.getId();
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtAnio.setText(String.valueOf(libro.getAnioPublicacion()));
        txtPrecio.setText(libro.getPrecio().toPlainString());

        if (libro.getCategoria() != null) {
            cmbCategoria.getItems().stream()
                    .filter(c -> c.getId().equals(libro.getCategoria().getId()))
                    .findFirst()
                    .ifPresent(cmbCategoria::setValue);
        } else {
            cmbCategoria.setValue(null);
        }

        actualizarBotones();
    }

    private void actualizarBotones() {
        boolean sinSeleccion = idSeleccionado == null;
        btnModificar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
    }

    private Libro leerFormulario() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();

        if (titulo.isEmpty() || autor.isEmpty()
                || txtAnio.getText().isBlank() || txtPrecio.getText().isBlank()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        Categoria categoria = cmbCategoria.getValue();
        if (categoria == null) {
            throw new IllegalArgumentException("Seleccione una categoría.");
        }

        int anio = Integer.parseInt(txtAnio.getText().trim());
        BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
        return new Libro(titulo, autor, anio, precio, categoria);
    }

    @FXML
    private void guardar() {
        try {
            libroDAO.guardar(leerFormulario());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Libro guardado correctamente.");
            cargar();
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Año y precio deben ser números válidos.");
        } catch (IllegalArgumentException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, ex.getMessage());
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar: " + ex.getMessage());
        }
    }

    @FXML
    private void modificar() {
        try {
            Libro libro = leerFormulario();
            libro.setId(idSeleccionado);

            if (libroDAO.modificar(libro)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Libro modificado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "El libro ya no existe en la base de datos.");
            }
            cargar();
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Año y precio deben ser números válidos.");
        } catch (IllegalArgumentException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, ex.getMessage());
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo modificar: " + ex.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        try {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("Eliminar libro");
            confirmacion.setContentText("¿Está seguro que desea eliminar el registro seleccionado?");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                return;
            }

            if (libroDAO.eliminar(idSeleccionado)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Libro eliminado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "El libro ya no existe en la base de datos.");
            }
            cargar();
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar: " + ex.getMessage());
        }
    }

    @FXML
    private void cargar() {
        try {
            List<Libro> libros = libroDAO.listar();
            tblLibros.setItems(FXCollections.observableArrayList(libros));
            limpiar();
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo cargar: " + ex.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        tblLibros.getSelectionModel().clearSelection();
        idSeleccionado = null;
        txtTitulo.clear();
        txtAutor.clear();
        txtAnio.clear();
        txtPrecio.clear();
        cmbCategoria.setValue(null);
        actualizarBotones();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}