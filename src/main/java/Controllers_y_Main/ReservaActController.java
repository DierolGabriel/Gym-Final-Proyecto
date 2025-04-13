package Controllers_y_Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservaActController {

    @FXML private Button Guardar;
    @FXML private Button Limpiar;
    @FXML private TextField Notificador;
    @FXML private Button Salir;
    @FXML private DatePicker fechaBajaPicker;
    @FXML private DatePicker fechaReservaPicker;
    @FXML private ComboBox<String> txtIdActividad;
    @FXML private ComboBox<String> txtIdClienteResAct;
    @FXML private ComboBox<String> txtIdEstadoReservaAct;
    @FXML private ComboBox<String> txtIdResHorAct;
    @FXML private TextField txtIdReservaActividad;

    private static final String ARCHIVO_RESERVAS_ACT = "Reservas Actividades.txt";
    private boolean modificando = false;

    @FXML
    public void initialize()
    {
        txtIdReservaActividad.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                validarIdReservaActividad(newValue);
            } else {
                limpiarCampos(true);
                desactivarCampos();
                Notificador.clear();
                modificando = false;
            }});
        cargarClientes();
        cargarEstados();
        cargarActividad();
        cargarHorarios();

    }

    private void validarIdReservaActividad(String idReserva) {
        File archivo = new File(ARCHIVO_RESERVAS_ACT);

        if (!archivo.exists()) {
            Notificador.setText("Creando");
            activarCampos();
            fechaReservaPicker.setValue(LocalDate.now());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean encontrado = false;

            while ((linea = br.readLine()) != null && !linea.isEmpty()) {
                String[] partes = linea.split(":");
                if (partes.length >= 7 && partes[0].equals(idReserva)) {
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
                fechaReservaPicker.setValue(LocalDate.now());
                modificando = false;
            }

        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de reservas: " + e.getMessage());
        }
    }

    private void cargarDatosReserva(String[] datos) {
        if (datos.length >= 7) {
            fechaReservaPicker.setValue(LocalDate.parse(datos[1], DateTimeFormatter.ofPattern("d/M/yyyy")));
            fechaBajaPicker.setValue(datos[2].isEmpty() ? null : LocalDate.parse(datos[2], DateTimeFormatter.ofPattern("d/M/yyyy")));
            txtIdClienteResAct.setValue(datos[3]);
            txtIdEstadoReservaAct.setValue(datos[4]);
            txtIdActividad.setValue(datos[5]);
            txtIdResHorAct.setValue(datos[6]);
        }
    }

    @FXML
    void Guardar(javafx.event.ActionEvent event)
    {
        if (!validarCampos())
        {
            return;
        }

        String idReserva = txtIdReservaActividad.getText().trim();
        String fechaReserva = fechaReservaPicker.getValue().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        String fechaBaja = fechaBajaPicker.getValue() == null ? "" : fechaBajaPicker.getValue().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        String idCliente = txtIdClienteResAct.getValue().trim();
        String idEstado = txtIdEstadoReservaAct.getValue().trim();
        String idActividad = txtIdActividad.getValue().trim();
        String idHorario = txtIdResHorAct.getValue().trim();

        String linea = String.join(":",
                idReserva, fechaReserva, fechaBaja, idCliente, idEstado, idActividad, idHorario
        );

        File archivo = new File(ARCHIVO_RESERVAS_ACT);
        List<String> lineas = new ArrayList<>();
        boolean existe = false;

        try {
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String lineaActual;
                    while ((lineaActual = br.readLine()) != null) {
                        if (!lineaActual.trim().isEmpty()) {
                            String[] partes = lineaActual.split(":");
                            if (partes.length > 0 && partes[0].equals(idReserva)) {
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

            mostrarAlerta("Reserva de actividad " + (existe ? "modificada" : "creada") + " exitosamente");
            Notificador.setText(existe ? "Modificado" : "Creado");
            limpiarCampos(true);

        } catch (IOException e) {
            mostrarAlerta("Error al guardar la reserva: " + e.getMessage());
        }
    }

    @FXML
    void Limpiar(javafx.event.ActionEvent event) {
        limpiarCampos(true);
        Notificador.clear();
        modificando = false;
    }

    @FXML
    void Salir(javafx.event.ActionEvent event) {
        Stage stageActual = (Stage) Notificador.getScene().getWindow();
        stageActual.close();
    }

    private void limpiarCampos(boolean incluirId) {
        if (incluirId) {
            txtIdReservaActividad.clear();
        }
        fechaReservaPicker.setValue(null);
        fechaBajaPicker.setValue(null);
        txtIdClienteResAct.setValue(null);
        txtIdEstadoReservaAct.setValue(null);
        txtIdActividad.setValue(null);
        txtIdResHorAct.setValue(null);
        desactivarCampos();
    }

    private boolean validarCampos() {
        if (txtIdReservaActividad.getText().trim().isEmpty() ||
                fechaReservaPicker.getValue() == null ||
                txtIdClienteResAct.getValue() == null ||
                txtIdEstadoReservaAct.getValue() == null ||
                txtIdResHorAct.getValue() == null ||
                txtIdActividad.getValue() == null)
        {
            mostrarAlerta("Todos los campos son obligatorios");
            return false;
        }
        try
        {
            int idReserva = Integer.parseInt(txtIdReservaActividad.getText());
        }
        catch (NumberFormatException e)
        {
            mostrarAlerta("La id de la reserva debe ser entero");
            return false;
        }
        return true;
    }

    private void activarCampos() {
        fechaReservaPicker.setDisable(false);
        fechaBajaPicker.setDisable(false);
        txtIdClienteResAct.setDisable(false);
        txtIdEstadoReservaAct.setDisable(false);
        txtIdActividad.setDisable(false);
        txtIdResHorAct.setDisable(false);
    }

    private void desactivarCampos() {
        fechaReservaPicker.setDisable(true);
        fechaBajaPicker.setDisable(true);
        txtIdClienteResAct.setDisable(true);
        txtIdEstadoReservaAct.setDisable(true);
        txtIdActividad.setDisable(true);
        txtIdResHorAct.setDisable(true);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarActividad() {
        File archivo = new File("Actividades.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            txtIdActividad.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        txtIdActividad.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Salas: " + e.getMessage());
        }
    }//Fin de cargarSalas

    private void cargarClientes() {
        File archivo = new File("Clientes.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            txtIdClienteResAct.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        txtIdClienteResAct.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Clientes: " + e.getMessage());
        }
    }//Fin de cargarClientes

    private void cargarHorarios()
    {
        File archivo = new File("Horarios_Actividades.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            txtIdResHorAct.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        txtIdResHorAct.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Horario: " + e.getMessage());
        }
    }//fin de horarios

    private void cargarEstados() {
        File archivo = new File("ReservasEstado.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            txtIdEstadoReservaAct.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        txtIdEstadoReservaAct.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Estado: " + e.getMessage());
        }
    }//fin de Estados

}