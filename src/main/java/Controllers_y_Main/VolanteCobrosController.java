package Controllers_y_Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class VolanteCobrosController {
    @FXML
    private Button Imprimir;

    @FXML
    private Button Salir;

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
                    if (partes.length >= 6 && partes[5].equals("false"))
                    {
                        try {
                            int id = Integer.parseInt(partes[0].trim());
                            String fecha = partes[1].trim();
                            String idCliente = partes[2].trim();
                            double valor = Double.parseDouble(partes[3].trim());
                            String concepto = partes[4].trim();
                            boolean status = false;

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
    @FXML
    public void imprimirPDF() {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            //Fuente
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            // Título
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("REPORTE DE COBROS");
            contentStream.endText();

            // Fecha
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 680);
            contentStream.showText("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            contentStream.endText();


            contentStream.setFont(PDType1Font.HELVETICA, 10);
            float yPosition = 650;

            String[] headers = {"ID", "Fecha", "Cliente", "Valor", "Concepto", "Estado"};
            float[] positions = {50, 100, 150, 250, 350, 450};

            for (int i = 0; i < headers.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(positions[i], yPosition);
                contentStream.showText(headers[i]);
                contentStream.endText();
            }

            yPosition -= 20;

            // Datos
            for (Cobro cobro : listaCobros) {
                String[] row = {
                        String.valueOf(cobro.getIdCobro()),
                        cobro.getFechaCobro(),
                        cobro.getIdCliente(),
                        "$" + cobro.getValorCobro(),
                        cobro.getConcepto(),
                        cobro.isStatus() ? "True" : "False"
                };

                for (int i = 0; i < row.length; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(positions[i], yPosition);
                    contentStream.showText(row[i]);
                    contentStream.endText();
                }

                yPosition -= 15;
            }

            contentStream.close();

            // Guardar y abrir PDF
            String fileName = "ReporteCobros.pdf";
            document.save(fileName);
            document.close();

            // Abrir el PDF automáticamente
            File file = new File(fileName);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                mostrarAlerta("No se puede abrir el PDF automáticamente");
            }

        } catch (IOException e) {
            mostrarAlerta("Error al generar PDF: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void salir()
    {
        Stage stageActual = (Stage) Salir.getScene().getWindow();
        stageActual.close();
    }

}