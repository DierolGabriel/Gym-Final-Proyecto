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

public class ConActividadesController {

    @FXML
    private Button Consultar;

    @FXML
    private RadioButton Filtro1;

    @FXML
    private RadioButton Filtro2;

    @FXML
    private Button Limpiar;

    @FXML
    private TableView<Actividad> Table;

    @FXML
    private TextField TextField;

    @FXML
    private TableColumn<Actividad, String> colIdAct;

    @FXML
    private TableColumn<Actividad, String> colNombreAct;

    @FXML
    private TableColumn<Actividad, String> colDescripcionAct;

    @FXML
    private TableColumn<Actividad, String> colIdLocalizacionAct;

    @FXML
    private TableColumn<Actividad, String> colIdEntrenadorAct;

    private static final String ARCHIVO_ACTIVIDADES = "Actividades.txt";
    private ObservableList<Actividad> listaActividades = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        colIdAct.setCellValueFactory(new PropertyValueFactory<>("idAct"));
        colNombreAct.setCellValueFactory(new PropertyValueFactory<>("nombreAct"));
        colDescripcionAct.setCellValueFactory(new PropertyValueFactory<>("descripcionAct"));
        colIdLocalizacionAct.setCellValueFactory(new PropertyValueFactory<>("idLocalizacionAct"));
        colIdEntrenadorAct.setCellValueFactory(new PropertyValueFactory<>("idEntrenadorAct"));

        // Cargar los datos del archivo
        cargarActividades();
    }

    private void cargarActividades() {
        listaActividades.clear();
        File archivo = new File(ARCHIVO_ACTIVIDADES);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de actividades no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length == 5) {
                        Actividad actividad = new Actividad(
                                partes[0].trim(), // idAct
                                partes[1].trim(), // nombreAct
                                partes[2].trim(), // descripcionAct
                                partes[3].trim(), // idLocalizacionAct
                                partes[4].trim()  // idEntrenadorAct
                        );
                        listaActividades.add(actividad);
                    }
                }
            }
            Table.setItems(listaActividades);
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de actividades: " + e.getMessage());
        }
    }

    @FXML
    void Consultar(ActionEvent event) {
        String filtro = TextField.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            Table.setItems(listaActividades);
            return;
        }

        ObservableList<Actividad> actividadesFiltradas = FXCollections.observableArrayList();

        for (Actividad actividad : listaActividades) {
            if (Filtro1.isSelected()) {
                // Filtrar por ID de actividad
                if (actividad.getIdAct().toLowerCase().contains(filtro)) {
                    actividadesFiltradas.add(actividad);
                }
            } else if (Filtro2.isSelected()) {
                // Filtrar por nombre de actividad
                if (actividad.getNombreAct().toLowerCase().contains(filtro)) {
                    actividadesFiltradas.add(actividad);
                }
            }
        }

        Table.setItems(actividadesFiltradas);
    }

    @FXML
    void Limpiar(ActionEvent event) {
        TextField.clear();
        Table.setItems(listaActividades);
        // Deseleccionar los radio buttons
        Filtro1.setSelected(false);
        Filtro2.setSelected(false);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class Actividad {
        private String idAct;
        private String nombreAct;
        private String descripcionAct;
        private String idLocalizacionAct;
        private String idEntrenadorAct;

        public Actividad(String idAct, String nombreAct, String descripcionAct, String idLocalizacionAct, String idEntrenadorAct) {
            this.idAct = idAct;
            this.nombreAct = nombreAct;
            this.descripcionAct = descripcionAct;
            this.idLocalizacionAct = idLocalizacionAct;
            this.idEntrenadorAct = idEntrenadorAct;
        }

        public String getIdAct() { return idAct; }
        public String getNombreAct() { return nombreAct; }
        public String getDescripcionAct() { return descripcionAct; }
        public String getIdLocalizacionAct() { return idLocalizacionAct; }
        public String getIdEntrenadorAct() { return idEntrenadorAct; }
    }
}