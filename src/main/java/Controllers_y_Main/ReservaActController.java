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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReservaActController {

    // Componentes FXML
    @FXML private TextField txtIdReservaActividad;
    @FXML private DatePicker fechaReservaPicker;
    @FXML private DatePicker fechaBajaPicker;
    @FXML private ComboBox<String> txtIdClienteResAct;
    @FXML private ComboBox<String> txtIdEstadoReservaAct;
    @FXML private ComboBox<String> txtIdActividad;
    @FXML private ComboBox<String> txtIdResHorAct;
    @FXML private TextField Notificador;
    @FXML private Button Guardar;
    @FXML private Button Limpiar;
    @FXML private Button Salir;

    // Archivos
    private static final String ARCHIVO_RESERVAS_ACT = "Reservas Actividades.txt";
    private static final String ARCHIVO_CLIENTES = "Clientes.txt";
    private static final String ARCHIVO_ESTADOS = "ReservasEstado.txt";
    private static final String ARCHIVO_ACTIVIDADES = "Actividades.txt";
    private static final String ARCHIVO_HORARIOS = "Horarios_Actividades.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    // Listas para combobox
    private ObservableList<String> clientes = FXCollections.observableArrayList();
    private ObservableList<String> estados = FXCollections.observableArrayList();
    private ObservableList<String> actividades = FXCollections.observableArrayList();
    private ObservableList<String> horarios = FXCollections.observableArrayList();

    // Estado
    private boolean modificando = false;

    @FXML
    public void initialize() {
        configurarListeners();
        cargarDatosCombobox();
    }

    private void configurarListeners() {
        txtIdReservaActividad.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                limpiarCampos(true);
                desactivarCampos();
                Notificador.clear();
                modificando = false;
            } else {
                validarIdReservaActividad(newValue.trim());
            }
        });
    }

    private void cargarDatosCombobox() {
        cargarClientes();
        cargarEstados();
        cargarActividades();
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
            boolean encontrado = false;

            String linea;
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
            mostrarAlerta("Error al leer reservas de actividades", AlertType.ERROR);
        }
    }

    private void cargarDatosReserva(String[] datos) {
        if (datos.length >= 7) {
            try {
                fechaReservaPicker.setValue(LocalDate.parse(datos[1], DATE_FORMATTER));
                fechaBajaPicker.setValue(datos[2].isEmpty() ? null : LocalDate.parse(datos[2], DATE_FORMATTER));
                txtIdClienteResAct.setValue(datos[3]);
                txtIdEstadoReservaAct.setValue(datos[4]);
                txtIdActividad.setValue(datos[5]);
                txtIdResHorAct.setValue(datos[6]);
            } catch (Exception e) {
                mostrarAlerta("Error al cargar datos de reserva", AlertType.ERROR);
            }
        }
    }

    @FXML
    private void guardar(javafx.event.ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        // Validar que las referencias existan
        if (!clienteExiste(txtIdClienteResAct.getValue())) {
            mostrarAlerta("El cliente seleccionado no existe", AlertType.ERROR);
            return;
        }

        if (!estadoExiste(txtIdEstadoReservaAct.getValue())) {
            mostrarAlerta("El estado seleccionado no existe", AlertType.ERROR);
            return;
        }

        if (!actividadExiste(txtIdActividad.getValue())) {
            mostrarAlerta("La actividad seleccionada no existe", AlertType.ERROR);
            return;
        }

        if (!horarioExiste(txtIdResHorAct.getValue())) {
            mostrarAlerta("El horario seleccionado no existe", AlertType.ERROR);
            return;
        }

        String linea = crearLineaReserva();
        List<String> lineas = new ArrayList<>();

        try {
            lineas = actualizarArchivoReservas(linea);
            guardarArchivoReservas(lineas);

            mostrarAlerta("Reserva de actividad " + (modificando ? "modificada" : "creada") + " exitosamente",
                    AlertType.INFORMATION);
            Notificador.setText(modificando ? "Modificado" : "Creado");
            limpiarCampos(true);

        } catch (IOException e) {
            mostrarAlerta("Error al guardar la reserva", AlertType.ERROR);
        }
    }

    private List<String> actualizarArchivoReservas(String nuevaLinea) throws IOException {
        List<String> lineas = new ArrayList<>();
        File archivo = new File(ARCHIVO_RESERVAS_ACT);

        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String lineaActual;
                while ((lineaActual = br.readLine()) != null) {
                    if (!lineaActual.trim().isEmpty()) {
                        String[] partes = lineaActual.split(":");
                        if (partes.length > 0 && partes[0].equals(txtIdReservaActividad.getText().trim())) {
                            lineas.add(nuevaLinea);
                            modificando = true;
                        } else {
                            lineas.add(lineaActual);
                        }
                    }
                }
            }
        }

        if (!modificando) {
            lineas.add(nuevaLinea);
        }

        return lineas;
    }

    private void guardarArchivoReservas(List<String> lineas) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_RESERVAS_ACT))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
        }
    }

    private String crearLineaReserva() {
        return String.join(":",
                txtIdReservaActividad.getText().trim(),
                fechaReservaPicker.getValue().format(DATE_FORMATTER),
                fechaBajaPicker.getValue() == null ? "" : fechaBajaPicker.getValue().format(DATE_FORMATTER),
                txtIdClienteResAct.getValue().trim(),
                txtIdEstadoReservaAct.getValue().trim(),
                txtIdActividad.getValue().trim(),
                txtIdResHorAct.getValue().trim()
        );
    }

    // Métodos de validación de existencia
    private boolean clienteExiste(String idCliente) {
        return idCliente != null && clientes.contains(idCliente);
    }

    private boolean estadoExiste(String idEstado) {
        return idEstado != null && estados.contains(idEstado);
    }

    private boolean actividadExiste(String idActividad) {
        return idActividad != null && actividades.contains(idActividad);
    }

    private boolean horarioExiste(String idHorario) {
        return idHorario != null && horarios.contains(idHorario);
    }

    private boolean validarCampos() {
        if (txtIdReservaActividad.getText().trim().isEmpty() ||
                fechaReservaPicker.getValue() == null ||
                txtIdClienteResAct.getValue() == null ||
                txtIdEstadoReservaAct.getValue() == null ||
                txtIdActividad.getValue() == null ||
                txtIdResHorAct.getValue() == null) {

            mostrarAlerta("Todos los campos son obligatorios (excepto fecha de baja)", AlertType.ERROR);
            return false;
        }

        try {
            Integer.parseInt(txtIdReservaActividad.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("El ID de reserva debe ser un número entero", AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    private void limpiar(javafx.event.ActionEvent event) {
        limpiarCampos(true);
        Notificador.clear();
        modificando = false;
    }

    @FXML
    private void salir(javafx.event.ActionEvent event) {
        ((Stage) Notificador.getScene().getWindow()).close();
    }

    private void limpiarCampos(boolean limpiarId) {
        if (limpiarId) {
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

    // Métodos para cargar datos en combobox
    private void cargarClientes() {
        cargarDatosArchivo(ARCHIVO_CLIENTES, clientes, txtIdClienteResAct, 0);
    }

    private void cargarEstados() {
        cargarDatosArchivo(ARCHIVO_ESTADOS, estados, txtIdEstadoReservaAct, 0);
    }

    private void cargarActividades() {
        cargarDatosArchivo(ARCHIVO_ACTIVIDADES, actividades, txtIdActividad, 0);
    }

    private void cargarHorarios() {
        cargarDatosArchivo(ARCHIVO_HORARIOS, horarios, txtIdResHorAct, 0);
    }

    private void cargarDatosArchivo(String archivoRuta, ObservableList<String> lista, ComboBox<String> comboBox, int indice) {
        lista.clear();
        File archivo = new File(archivoRuta);

        if (!archivo.exists()) {
            mostrarAlerta("Archivo no encontrado: " + archivoRuta, AlertType.WARNING);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length > indice) {
                        lista.add(partes[indice].trim());
                    }
                }
            }
            comboBox.setItems(lista);
        } catch (IOException e) {
            mostrarAlerta("Error al cargar " + archivoRuta, AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == AlertType.ERROR ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}