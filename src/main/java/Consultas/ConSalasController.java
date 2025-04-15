package Consultas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConSalasController {

    @FXML
    private Button Consultar;

    @FXML
    private RadioButton Filtro1;

    @FXML
    private RadioButton Filtro2;

    @FXML
    private Button Limpiar;

    @FXML
    private TableView<Sala> Table;

    @FXML
    private TextField TextField;

    @FXML
    private TableColumn<Sala, String> colId;

    @FXML
    private TableColumn<Sala, String> colIdLocalizacion;

    @FXML
    private TableColumn<Sala, String> colNombre;

    @FXML
    private TableColumn<Sala, String> colDescripcion;

    private static final String ARCHIVO_SALAS = "Salas.txt";
    private ObservableList<Sala> listaSalas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdLocalizacion.setCellValueFactory(new PropertyValueFactory<>("idLocalizacion"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Cargar los datos del archivo
        cargarSalas();
        configurarListenerID();
    }

    private void configurarListenerID() {
        TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty())
            {
                Consultar(null);
            }
            else
            {
                Consultar(null);
            }
        });
    }

    private void cargarSalas() {
        listaSalas.clear();
        File archivo = new File(ARCHIVO_SALAS);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de salas no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length == 4) {
                        Sala sala = new Sala(
                                partes[0].trim(), // id
                                partes[1].trim(), // idLocalizacion
                                partes[2].trim(), // nombre
                                partes[3].trim()  // descripcion
                        );
                        listaSalas.add(sala);
                    }
                }
            }
            Table.setItems(listaSalas);
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de salas: " + e.getMessage());
        }
    }

    @FXML
    void Consultar(ActionEvent event) {
        String filtro = TextField.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            Table.setItems(listaSalas);
            return;
        }

        ObservableList<Sala> salasFiltradas = FXCollections.observableArrayList();

        for (Sala sala : listaSalas) {
            if (Filtro1.isSelected()) {
                // Filtrar por Nombre
                if (sala.getNombre().toLowerCase().contains(filtro)) {
                    salasFiltradas.add(sala);
                }
            } else if (Filtro2.isSelected()) {
                // Filtrar por Usuario (asumiendo que se refiere a ID Localización)
                if (sala.getIdLocalizacion().toLowerCase().contains(filtro)) {
                    salasFiltradas.add(sala);
                }
            }
        }

        Table.setItems(salasFiltradas);
    }

    @FXML
    void Limpiar(ActionEvent event) {
        Stage stageActual = (Stage) TextField.getScene().getWindow();
        stageActual.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class Sala {
        private String id;
        private String idLocalizacion;
        private String nombre;
        private String descripcion;

        public Sala(String id, String idLocalizacion, String nombre, String descripcion) {
            this.id = id;
            this.idLocalizacion = idLocalizacion;
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        public String getId() { return id; }
        public String getIdLocalizacion() { return idLocalizacion; }
        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
    }
}