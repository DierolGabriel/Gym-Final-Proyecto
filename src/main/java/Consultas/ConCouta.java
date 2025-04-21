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

public class ConCouta {

    @FXML
    private TextField Consultar;

    @FXML
    private TableColumn<Cuota, String> Fecha;

    @FXML
    private RadioButton Filtro1;

    @FXML
    private RadioButton Filtro2;

    @FXML
    private ToggleGroup Grupo1;

    @FXML
    private TableColumn<Cuota, String> IDCuota;

    @FXML
    private TableColumn<Cuota, String> IDcliente;

    @FXML
    private Button Salir;

    @FXML
    private TableView<Cuota> Table;

    @FXML
    private TableColumn<Cuota, String> status;

    @FXML
    private TableColumn<Cuota, String> valor;

    private static final String ARCHIVO_CUOTAS = "Cuota.txt";
    private ObservableList<Cuota> listaCuotas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        IDCuota.setCellValueFactory(new PropertyValueFactory<>("idCuota"));
        Fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        IDcliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        valor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Cargar las cuotas del archivo
        cargarCuotas();

        // Configurar el listener para el campo de consulta
        configurarListenerConsulta();

        // Configurar los listeners para los radio buttons
        configurarListenersFiltros();
    }

    private void configurarListenerConsulta() {
        Consultar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                Consultar(null);
            } else {
                Consultar(null);
            }
        });
    }

    private void configurarListenersFiltros() {
        Filtro1.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Consultar.setPromptText("Consultar");
                Consultar.setText("");
            }
        });

        Filtro2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Consultar.setPromptText("DD/MM/AAAA");
                Consultar.setText("");
            }
        });
    }

    private void cargarCuotas() {
        listaCuotas.clear();
        File archivo = new File(ARCHIVO_CUOTAS);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de cuotas no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 5) {
                        Cuota cuota = new Cuota(
                                partes[0], // idCuota
                                partes[1], // idCliente
                                partes[2], // valor
                                partes[3], // fecha
                                partes[4]  // status
                        );
                        listaCuotas.add(cuota);
                    }
                }
            }
            Table.setItems(listaCuotas);
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de cuotas: " + e.getMessage());
        }
    }

    @FXML
    void Consultar(ActionEvent event) {
        String filtro = Consultar.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            Table.setItems(listaCuotas);
            return;
        }

        ObservableList<Cuota> cuotasFiltradas = FXCollections.observableArrayList();

        for (Cuota cuota : listaCuotas) {
            if (Filtro1.isSelected()) {
                // Filtrar por ID de cliente
                if (cuota.getIdCliente().toLowerCase().contains(filtro)) {
                    cuotasFiltradas.add(cuota);
                }
            } else if (Filtro2.isSelected()) {
                // Filtrar por fecha (formato DD/MM/AAAA)
                if (cuota.getFecha().toLowerCase().contains(filtro)) {
                    cuotasFiltradas.add(cuota);
                }
            }
        }

        Table.setItems(cuotasFiltradas);
    }

    @FXML
    void Salir(ActionEvent event) {
        Stage stageActual = (Stage) Salir.getScene().getWindow();
        stageActual.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class Cuota {
        private String idCuota;
        private String idCliente;
        private String valor;
        private String fecha;
        private String status;

        public Cuota(String idCuota, String idCliente, String valor, String fecha, String status) {
            this.idCuota = idCuota;
            this.idCliente = idCliente;
            this.valor = valor;
            this.fecha = fecha;
            this.status = status;
        }

        public String getIdCuota() { return idCuota; }
        public String getIdCliente() { return idCliente; }
        public String getValor() { return valor; }
        public String getFecha() { return fecha; }
        public String getStatus() { return status; }
    }
}