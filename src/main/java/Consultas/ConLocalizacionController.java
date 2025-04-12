package Consultas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConLocalizacionController {

    @FXML
    private Button Consultar;

    @FXML
    private RadioButton Filtro1;

    @FXML
    private RadioButton Filtro2;

    @FXML
    private ToggleGroup Grupo1;

    @FXML
    private Button Limpiar;

    @FXML
    private TableView<Localizacion> Table;

    @FXML
    private TextField TextField;

    @FXML
    private TableColumn<Localizacion, String> colID;

    @FXML
    private TableColumn<Localizacion, String> colTipo;

    private static final String ARCHIVO_LOCALIZACION = "Localización.txt";
    private ObservableList<Localizacion> listaLocalizaciones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        
        cargarLocalizaciones();
    }

    private void cargarLocalizaciones() {
        listaLocalizaciones.clear();
        File archivo = new File(ARCHIVO_LOCALIZACION);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de localizaciones no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length == 2) {
                        Localizacion localizacion = new Localizacion(
                                partes[0].trim(),
                                partes[1].trim()
                        );
                        listaLocalizaciones.add(localizacion);
                    }
                }
            }
            Table.setItems(listaLocalizaciones);
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de localizaciones: " + e.getMessage());
        }
    }

    @FXML
    void Consultar(ActionEvent event) {
        String filtro = TextField.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            Table.setItems(listaLocalizaciones);
            return;
        }

        ObservableList<Localizacion> localizacionesFiltradas = FXCollections.observableArrayList();

        for (Localizacion localizacion : listaLocalizaciones) {
            if (Filtro1.isSelected()) {
                if (localizacion.getId().toLowerCase().contains(filtro)) {
                    localizacionesFiltradas.add(localizacion);
                }
            } else if (Filtro2.isSelected()) {
                if (localizacion.getTipo().toLowerCase().contains(filtro)) {
                    localizacionesFiltradas.add(localizacion);
                }
            }
        }

        Table.setItems(localizacionesFiltradas);
    }

    @FXML
    void Limpiar(ActionEvent event) {
        TextField.clear();
        Table.setItems(listaLocalizaciones);
        Grupo1.selectToggle(null);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class Localizacion {
        private String id;
        private String tipo;

        public Localizacion(String id, String tipo) {
            this.id = id;
            this.tipo = tipo;
        }

        public String getId() {
            return id;
        }

        public String getTipo() {
            return tipo;
        }
    }
}