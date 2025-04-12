package Controllers_y_Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservaController {

    @FXML private TextField idReserva;
    @FXML private TextField idSalaReserva;
    @FXML private TextField idClienteReserva;
    @FXML private DatePicker fechaReserva;
    @FXML private TextField idHorarioReserva;
    @FXML private TextField IdEstadoReserva;
    @FXML private TextField Notificador;
    @FXML private Button Guardar;
    @FXML private Button Limpiar;
    @FXML private Button Salir;

    private static final String ARCHIVO_RESERVAS = "Reserva.txt";
    private static final String ARCHIVO_SALAS = "Salas.txt";
    private static final String ARCHIVO_CLIENTES = "Clientes.txt";
    private static final String ARCHIVO_HORARIOS = "Horarios_Actividades.txt";
    private static final String ARCHIVO_ESTADOS = "ReservasEstado.txt";
    private boolean modificando = false;

    @FXML
    public void initialize() {
        idReserva.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                validarIdReserva(newValue);
            }
            else
            {
                limpiarCampos(true);
                desactivarCampos();
            }
        });
    }

    private void validarIdReserva(String idReserva) {
        File archivo = new File(ARCHIVO_RESERVAS);

        if (!archivo.exists()) {
            Notificador.setText("Creando");
            activarCampos();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean encontrado = false;

            while ((linea = br.readLine()) != null && !linea.isEmpty()) {
                String[] partes = linea.split(":");
                if (partes.length >= 6 && partes[0].equals(idReserva)) {
                    cargarDatosReserva(partes);
                    encontrado = true;
                    modificando = true;
                    Notificador.setText("Modificando");
                    break;
                }
            }

            if (!encontrado) {
                Notificador.setText("Creando");
                limpiarCampos(false);
                activarCampos();
                fechaReserva.setValue(LocalDate.now());
                modificando = false;
            }

        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de reservas: " + e.getMessage());
        }
    }

    private void cargarDatosReserva(String[] datos) {
        if (datos.length >= 6) {
            idSalaReserva.setText(datos[1]);
            idClienteReserva.setText(datos[2]);
            fechaReserva.setValue(LocalDate.parse(datos[3], DateTimeFormatter.ofPattern("d/M/yyyy")));
            idHorarioReserva.setText(datos[4]);
            IdEstadoReserva.setText(datos[5]);
        }
    }

    @FXML
    void Guardar(javafx.event.ActionEvent event)
    {
        if (!validarCampos()) {
            return;
        }

        if (!validarReferencias()) {
            return;
        }

        String id = idReserva.getText().trim();
        String idSala = idSalaReserva.getText().trim();
        String idCliente = idClienteReserva.getText().trim();
        String fecha = fechaReserva.getValue().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        String idHorario = idHorarioReserva.getText().trim();
        String idEstado = IdEstadoReserva.getText().trim();

        String linea = String.join(":", id, idSala, idCliente, fecha, idHorario, idEstado);

        File archivo = new File(ARCHIVO_RESERVAS);
        List<String> lineas = new ArrayList<>();
        boolean existe = false;

        try {
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String lineaActual;
                    while ((lineaActual = br.readLine()) != null) {
                        if (!lineaActual.trim().isEmpty()) {
                            String[] partes = lineaActual.split(":");
                            if (partes.length > 0 && partes[0].equals(id)) {
                                lineas.add(linea);
                                existe = true;
                            } else {
                                lineas.add(lineaActual);
                            }
                        }
                    }
                }
            }

            if (!existe) {
                lineas.add(linea);
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (String l : lineas) {
                    bw.write(l);
                    bw.newLine();
                }
            }

            mostrarAlerta("Reserva " + (existe ? "modificada" : "creada") + " exitosamente");
            Notificador.setText(existe ? "Modificado" : "Creado");

        } catch (IOException e) {
            mostrarAlerta("Error al guardar la reserva: " + e.getMessage());
        }
    }

    private boolean validarReferencias() {
        if (!existeEnArchivo(idSalaReserva.getText().trim(), ARCHIVO_SALAS)) {
            mostrarAlerta("No existe una sala con ese ID");
            return false;
        }

        if (!existeEnArchivo(idClienteReserva.getText().trim(), ARCHIVO_CLIENTES)) {
            mostrarAlerta("No existe un cliente con ese ID");
            return false;
        }

        if (!existeEnArchivo(idHorarioReserva.getText().trim(), ARCHIVO_HORARIOS)) {
            mostrarAlerta("No existe un horario con ese ID");
            return false;
        }

        if (!existeEnArchivo(IdEstadoReserva.getText().trim(), ARCHIVO_ESTADOS)) {
            mostrarAlerta("No existe un estado con ese ID");
            return false;
        }

        return true;
    }

    private boolean existeEnArchivo(String id, String archivoRuta) {
        File archivo = new File(archivoRuta);
        if (!archivo.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length > 0 && partes[0].equals(id)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo: " + archivoRuta);
        }
        return false;
    }

    @FXML
    void Limpiar(javafx.event.ActionEvent event)
    {
        limpiarCampos(true);
        Notificador.clear();
        modificando = false;
    }

    @FXML
    void Salir(javafx.event.ActionEvent event)
    {
        Stage stageActual = (Stage) Notificador.getScene().getWindow();
        stageActual.close();
    }

    private void limpiarCampos(boolean incluirId) {
        if (incluirId) {
            idReserva.clear();
        }
        idSalaReserva.clear();
        idClienteReserva.clear();
        fechaReserva.setValue(null);
        idHorarioReserva.clear();
        IdEstadoReserva.clear();
        desactivarCampos();
    }

    private boolean validarCampos() {
        if (idReserva.getText().trim().isEmpty() ||
                idSalaReserva.getText().trim().isEmpty() ||
                idClienteReserva.getText().trim().isEmpty() ||
                fechaReserva.getValue() == null ||
                idHorarioReserva.getText().trim().isEmpty() ||
                IdEstadoReserva.getText().trim().isEmpty()) {

            mostrarAlerta("Todos los campos son obligatorios");
            return false;
        }

        return true;
    }

    private void activarCampos() {
        idSalaReserva.setDisable(false);
        idClienteReserva.setDisable(false);
        fechaReserva.setDisable(false);
        idHorarioReserva.setDisable(false);
        IdEstadoReserva.setDisable(false);
    }

    private void desactivarCampos() {
        idSalaReserva.setDisable(true);
        idClienteReserva.setDisable(true);
        fechaReserva.setDisable(true);
        idHorarioReserva.setDisable(true);
        IdEstadoReserva.setDisable(true);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}