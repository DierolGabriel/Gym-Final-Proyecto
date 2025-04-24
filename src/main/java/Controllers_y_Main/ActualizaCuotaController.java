package Controllers_y_Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActualizaCuotaController {

    @FXML
    private DatePicker FechaFinal;

    @FXML
    private DatePicker FechaInicial;

    @FXML
    private Button Procesar;

    @FXML
    private Button Salir;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final String CLIENTES_FILE = "Clientes.txt";
    private static final String COBRO_FILE = "Cobro.txt";
    private static final String CUOTA_FILE = "Cuota.txt";

    @FXML
    void Procesar(ActionEvent event) {
        LocalDate fechaInicial = FechaInicial.getValue();
        LocalDate fechaFinal = FechaFinal.getValue();

        // Validar fechas
        if (fechaInicial == null || fechaFinal == null) {
            showAlert("Error", "Debe seleccionar ambas fechas.");
            return;
        }

        if (fechaInicial.isAfter(fechaFinal)) {
            showAlert("Error", "La fecha inicial debe ser anterior a la fecha final.");
            return;
        }

        try {
            // Leer cuotas que están dentro del rango y no están pagadas
            List<Cuota> cuotas = getCuotasEnRango(fechaInicial, fechaFinal);

            if (cuotas.isEmpty()) {
                showAlert("Información", "No hay cuotas pendientes en el rango de fechas seleccionado.");
                return;
            }

            // Procesar cada cuota
            for (Cuota cuota : cuotas) {
                procesarCuota(cuota);
            }

            showAlert("Éxito", "Se han actualizado las cuotas correctamente.");

        } catch (IOException e) {
            showAlert("Error", "Ocurrió un error al procesar los archivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void Salir(ActionEvent event) {
        Salir.getScene().getWindow().hide();
    }

    private List<Cuota> getCuotasEnRango(LocalDate fechaInicial, LocalDate fechaFinal) throws IOException {
        List<Cuota> cuotas = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(CUOTA_FILE));

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length >= 5) {
                String idCuota = parts[0];
                String idCliente = parts[1];
                double valor = Double.parseDouble(parts[2]);
                LocalDate fechaCuota = LocalDate.parse(parts[3], DATE_FORMATTER);
                boolean status = Boolean.parseBoolean(parts[4]);

                if (!status && !fechaCuota.isBefore(fechaInicial) && !fechaCuota.isAfter(fechaFinal)) {
                    cuotas.add(new Cuota(idCuota, idCliente, valor, fechaCuota, status));
                }
            }
        }

        return cuotas;
    }

    private void procesarCuota(Cuota cuota) throws IOException {
        // Actualizar cliente
        actualizarCliente(cuota.getIdCliente(), cuota.getValor());

        // Leer la cuota para ver qué cobros están asociados
        List<String> cobrosAsociados = getCobrosAsociadosACuota(cuota.getIdCuota());

        // Actualizar solo los cobros que están asociados a esta cuota
        boolean todosCobrosPagados = actualizarCobros(cuota.getIdCliente(), cuota.getValor(), cobrosAsociados);

        // Actualizar status de cuota si todos los cobros asociados están pagados
        if (todosCobrosPagados) {
            actualizarStatusCuota(cuota.getIdCuota());
        }
    }


    private List<String> getCobrosAsociadosACuota(String idCuota) throws IOException {
        List<String> cobrosAsociados = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(CUOTA_FILE));

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length >= 5 && parts[0].equals(idCuota)) {
                // Aquí podríamos tener un campo adicional que indique qué cobros están asociados
                // Por ahora asumiremos que todos los cobros del cliente están asociados
                // Esto debería modificarse según tu estructura de datos real
                cobrosAsociados.add(parts[1]); // idCliente
                break;
            }
        }
        return cobrosAsociados;
    }

    private void actualizarCliente(String idCliente, double valor) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(CLIENTES_FILE));
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length >= 12 && parts[0].equals(idCliente)) {

                double balance = Double.parseDouble(parts[10]);
                balance -= valor;
                parts[10] = String.format("%.1f", balance);

                line = String.join(":", parts);
            }
            newLines.add(line);
        }

        Files.write(Paths.get(CLIENTES_FILE), newLines);
    }

    private boolean actualizarCobros(String idCliente, double valor, List<String> cobrosAsociados) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(COBRO_FILE));
        List<String> newLines = new ArrayList<>();
        boolean todosPagados = true;

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length >= 6 && parts[2].equals(idCliente)) {
                double valorCobro = Double.parseDouble(parts[3]);
                boolean status = Boolean.parseBoolean(parts[5]);
                String idCobro = parts[0];

                // Solo actualizar cobros que están asociados a cuotas
                if (!status && cobrosAsociados.contains(idCobro)) {
                    valorCobro -= valor;
                    if (valorCobro <= 0) {
                        valorCobro = 0;
                        status = true;
                    } else {
                        todosPagados = false;
                    }

                    parts[3] = String.format("%.1f", valorCobro);
                    parts[5] = String.valueOf(status);
                    line = String.join(":", parts);
                }
            }
            newLines.add(line);
        }

        Files.write(Paths.get(COBRO_FILE), newLines);
        return todosPagados;
    }

    private void actualizarStatusCuota(String idCuota) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(CUOTA_FILE));
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length >= 5 && parts[0].equals(idCuota)) {
                parts[4] = "true";
                line = String.join(":", parts);
            }
            newLines.add(line);
        }

        Files.write(Paths.get(CUOTA_FILE), newLines);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class Cuota {
        private String idCuota;
        private String idCliente;
        private double valor;
        private LocalDate fecha;
        private boolean status;

        public Cuota(String idCuota, String idCliente, double valor, LocalDate fecha, boolean status) {
            this.idCuota = idCuota;
            this.idCliente = idCliente;
            this.valor = valor;
            this.fecha = fecha;
            this.status = status;
        }

        public String getIdCuota() { return idCuota; }
        public String getIdCliente() { return idCliente; }
        public double getValor() { return valor; }
        public LocalDate getFecha() { return fecha; }
        public boolean isStatus() { return status; }
    }
}