package Controllers_y_Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class VolanteCobrosController {

    @FXML
    private TableView<Cobro> tablaCobros;

    @FXML
    private TableColumn<Cobro, Integer> IDCobro;

    @FXML
    private TableColumn<Cobro, String> FechaCobro;

    @FXML
    private TableColumn<Cobro, String> IDCliente;

    @FXML
    private TableColumn<Cobro, Double> ValorCobro;

    @FXML
    private TableColumn<Cobro, String> ConceptoCobro;

    @FXML
    private TableColumn<Cobro, Boolean> Status;

    private static final String ARCHIVO_COBROS = "Cobro.txt";
    private ObservableList<Cobro> listaCobros = FXCollections.observableArrayList();

    // Clase interna para representar un cobro
    public static class Cobro {
        private final int idCobro;
        private final String fechaCobro;
        private final String idCliente;
        private final double valorCobro;
        private final String concepto;
        private final boolean status;

        public Cobro(int idCobro, String fechaCobro, String idCliente,
                     double valorCobro, String concepto, boolean status) {
            this.idCobro = idCobro;
            this.fechaCobro = fechaCobro;
            this.idCliente = idCliente;
            this.valorCobro = valorCobro;
            this.concepto = concepto;
            this.status = status;
        }

        // Getters
        public int getIdCobro() { return idCobro; }
        public String getFechaCobro() { return fechaCobro; }
        public String getIdCliente() { return idCliente; }
        public double getValorCobro() { return valorCobro; }
        public String getConcepto() { return concepto; }
        public boolean isStatus() { return status; }
        public String getStatusString() { return status ? "True" : "False"; }
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarCobros();
    }

    private void configurarColumnas() {
        IDCobro.setCellValueFactory(new PropertyValueFactory<>("idCobro"));
        FechaCobro.setCellValueFactory(new PropertyValueFactory<>("fechaCobro"));
        IDCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        ValorCobro.setCellValueFactory(new PropertyValueFactory<>("valorCobro"));
        ConceptoCobro.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        Status.setCellValueFactory(new PropertyValueFactory<>("statusString"));
    }

    private void cargarCobros() {
        listaCobros.clear();
        File archivo = new File(ARCHIVO_COBROS);

        if (!archivo.exists()) {
            System.out.println("El archivo de cobros no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 6) {
                        try {
                            int id = Integer.parseInt(partes[0].trim());
                            String fecha = partes[1].trim();
                            String idCliente = partes[2].trim();
                            double valor = Double.parseDouble(partes[3].trim());
                            String concepto = partes[4].trim();
                            boolean status = Boolean.parseBoolean(partes[5].trim());

                            listaCobros.add(new Cobro(id, fecha, idCliente, valor, concepto, status));
                        } catch (NumberFormatException e) {
                            System.err.println("Error al parsear datos del cobro: " + linea);
                        }
                    }
                }
            }
            tablaCobros.setItems(listaCobros);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de cobros: " + e.getMessage());
        }
    }
}