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

public class ConEntrenadorController {

    @FXML private Button Consultar;
    @FXML private RadioButton Filtro1;
    @FXML private RadioButton Filtro2;
    @FXML private Button Limpiar;
    @FXML private TableView<Entrenador> Table;
    @FXML private TextField TextField;
    @FXML private TableColumn<Entrenador, String> colId;
    @FXML private TableColumn<Entrenador, String> colNombre;
    @FXML private TableColumn<Entrenador, String> colApellido;
    @FXML private TableColumn<Entrenador, String> colTelefono;
    @FXML private TableColumn<Entrenador, String> colCorreo;
    @FXML private TableColumn<?, ?> colIdEntrenadorNombreAct;

    private static final String ARCHIVO_ENTRENADORES = "Entrenadores.txt";
    private ObservableList<Entrenador> listaEntrenadores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        ToggleGroup grupoFiltros = new ToggleGroup();
        Filtro1.setToggleGroup(grupoFiltros);
        Filtro2.setToggleGroup(grupoFiltros);

        cargarEntrenadores();
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

    private void cargarEntrenadores() {
        listaEntrenadores.clear();
        File archivo = new File(ARCHIVO_ENTRENADORES);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de entrenadores no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 5) {
                        Entrenador entrenador = new Entrenador(
                                partes[0],
                                partes[1],
                                partes[2],
                                partes[3],
                                partes[4]
                        );
                        listaEntrenadores.add(entrenador);
                    }
                }
            }
            Table.setItems(listaEntrenadores);
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de entrenadores: " + e.getMessage());
        }
    }

    @FXML
    void Consultar(ActionEvent event) {
        String filtro = TextField.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            Table.setItems(listaEntrenadores);
            return;
        }

        ObservableList<Entrenador> entrenadoresFiltrados = FXCollections.observableArrayList();

        for (Entrenador entrenador : listaEntrenadores) {
            if (Filtro1.isSelected()) {
                if (entrenador.getId().toLowerCase().contains(filtro)) {
                    entrenadoresFiltrados.add(entrenador);
                }
            } else if (Filtro2.isSelected()) {
                if (entrenador.getNombre().toLowerCase().contains(filtro)) {
                    entrenadoresFiltrados.add(entrenador);
                }
            }
        }
        Table.setItems(entrenadoresFiltrados);
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

    public static class Entrenador {
        private String id;
        private String nombre;
        private String apellido;
        private String telefono;
        private String correo;

        public Entrenador(String id, String nombre, String apellido, String telefono, String correo) {
            this.id = id;
            this.nombre = nombre;
            this.apellido = apellido;
            this.telefono = telefono;
            this.correo = correo;
        }

        public String getId() { return id; }
        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getTelefono() { return telefono; }
        public String getCorreo() { return correo; }
    }
}