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

public class ConHorariosController {

    @FXML
    private TableColumn<?, ?> ColDia;

    @FXML
    private TableColumn<?, ?> ColHora;

    @FXML
    private Button Consultar;

    @FXML
    private RadioButton Filtro1;

    @FXML
    private RadioButton Filtro2;

    @FXML
    private Button Limpiar;

    @FXML
    private TableView<Horario> Table;

    @FXML
    private TextField TextField;

    @FXML
    private TableColumn<Horario, String> colId;

    @FXML
    private TableColumn<Horario, String> colIdActividad;

    @FXML
    private TableColumn<Horario, String> colDia;

    @FXML
    private TableColumn<Horario, String> colHora;


    private static final String ARCHIVO_HORARIOS = "Horarios_Actividades.txt";
    private ObservableList<Horario> listaHorarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdActividad.setCellValueFactory(new PropertyValueFactory<>("idActividad"));
        colDia.setCellValueFactory(new PropertyValueFactory<>("dia"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("horaCompleta"));

        // Cargar los datos del archivo
        cargarHorarios();
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

    private void cargarHorarios() {
        listaHorarios.clear();
        File archivo = new File(ARCHIVO_HORARIOS);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de horarios no existe en: " + archivo.getAbsolutePath());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length == 5) {
                        // Combinar hora (partes[2]) y minuto (partes[3]) en formato HH:MM
                        String horaCompleta = partes[2].trim() + ":" + partes[3].trim();
                        Horario horario = new Horario(
                                partes[0].trim(),  // id
                                partes[1].trim(),  // idActividad
                                partes[4].trim(),  // dia
                                horaCompleta,      // hora completa (HH:MM)
                                partes[4].trim()   // idSala
                        );
                        listaHorarios.add(horario);
                    }
                }
            }
            Table.setItems(listaHorarios);
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de horarios: " + e.getMessage());
        }
    }

    @FXML
    void Consultar(ActionEvent event) {
        String filtro = TextField.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            Table.setItems(listaHorarios);
            return;
        }

        ObservableList<Horario> horariosFiltrados = FXCollections.observableArrayList();

        for (Horario horario : listaHorarios) {
            if (Filtro1.isSelected()) {
                // Filtrar por ID
                if (horario.getId().toLowerCase().contains(filtro)) {
                    horariosFiltrados.add(horario);
                }
            } else if (Filtro2.isSelected()) {
                // Filtrar por ID Actividad
                if (horario.getIdActividad().toLowerCase().contains(filtro)) {
                    horariosFiltrados.add(horario);
                }
            }
        }

        Table.setItems(horariosFiltrados);
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

    public static class Horario {
        private String id;
        private String idActividad;
        private String dia;
        private String horaCompleta;
        private String idSala;

        public Horario(String id, String idActividad, String dia, String horaCompleta, String idSala) {
            this.id = id;
            this.idActividad = idActividad;
            this.dia = dia;
            this.horaCompleta = horaCompleta;
            this.idSala = idSala;
        }

        public String getId() { return id; }
        public String getIdActividad() { return idActividad; }
        public String getDia() { return dia; }
        public String getHoraCompleta() { return horaCompleta; }
        public String getIdSala() { return idSala; }
    }
}