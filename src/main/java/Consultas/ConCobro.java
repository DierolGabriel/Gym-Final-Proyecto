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

public class ConCobro {

    @FXML
    private TextField Consultar;

    @FXML
    private TableColumn<Cobro, String> Fecha;

    @FXML
    private RadioButton Filtro1;

    @FXML
    private RadioButton Filtro2;

    @FXML
    private ToggleGroup Grupo1;

    @FXML
    private TableColumn<Cobro, String> IDCobro;

    @FXML
    private TableColumn<Cobro, String> IDcliente;

    @FXML
    private Button Salir;

    @FXML
    private TableView<Cobro> Table;

    @FXML
    private TableColumn<Cobro, String> concepto;

    @FXML
    private TableColumn<Cobro, String> status;

    @FXML
    private TableColumn<Cobro, String> valor;

    private static final String ARCHIVO_COBROS = "Cobro.txt";
    private ObservableList<Cobro> listaCobros = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        IDCobro.setCellValueFactory(new PropertyValueFactory<>("idCobro"));
        Fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        IDcliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        valor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        concepto.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Cargar los cobros del archivo
        cargarCobros();

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

    private void cargarCobros() {
        listaCobros.clear();
        File archivo = new File(ARCHIVO_COBROS);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de cobros no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 5) {
                        Cobro cobro = new Cobro(
                                partes[0], // idCobro
                                partes[1], // fecha
                                partes[2], // idCliente
                                partes[3], // valor
                                partes[4], // concepto
                                partes[5]  // status
                        );
                        listaCobros.add(cobro);
                    }
                }
            }
            Table.setItems(listaCobros);
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de cobros: " + e.getMessage());
        }
    }

    @FXML
    void Consultar(ActionEvent event) {
        String filtro = Consultar.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            Table.setItems(listaCobros);
            return;
        }

        ObservableList<Cobro> cobrosFiltrados = FXCollections.observableArrayList();

        for (Cobro cobro : listaCobros) {
            if (Filtro1.isSelected()) {
                // Filtrar por ID de cliente
                if (cobro.getIdCliente().toLowerCase().contains(filtro)) {
                    cobrosFiltrados.add(cobro);
                }
            } else if (Filtro2.isSelected()) {
                // Filtrar por fecha (formato DD/MM/AAAA)
                if (cobro.getFecha().toLowerCase().contains(filtro)) {
                    cobrosFiltrados.add(cobro);
                }
            }
        }

        Table.setItems(cobrosFiltrados);
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

    public static class Cobro {
        private String idCobro;
        private String fecha;
        private String idCliente;
        private String valor;
        private String concepto;
        private String status;

        public Cobro(String idCobro, String fecha, String idCliente, String valor, String concepto, String status) {
            this.idCobro = idCobro;
            this.fecha = fecha;
            this.idCliente = idCliente;
            this.valor = valor;
            this.concepto = concepto;
            this.status = status;
        }

        public String getIdCobro() { return idCobro; }
        public String getFecha() { return fecha; }
        public String getIdCliente() { return idCliente; }
        public String getValor() { return valor; }
        public String getConcepto() { return concepto; }
        public String getStatus() { return status; }
    }
}