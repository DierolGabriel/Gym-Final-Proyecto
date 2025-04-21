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

public class ReservaController {

    // Componentes FXML
    @FXML private TextField idReserva;
    @FXML private ComboBox<String> idSalaReserva;
    @FXML private ComboBox<String> idClienteReserva;
    @FXML private DatePicker fechaReserva;
    @FXML private ComboBox<String> idHorarioReserva;
    @FXML private ComboBox<String> IdEstadoReserva;
    @FXML private TextField Notificador;
    @FXML private Button Guardar;
    @FXML private Button Limpiar;
    @FXML private Button Salir;

    // Archivos
    private static final String ARCHIVO_RESERVAS = "Reserva.txt";
    private static final String ARCHIVO_SALAS = "Salas.txt";
    private static final String ARCHIVO_CLIENTES = "Clientes.txt";
    private static final String ARCHIVO_HORARIOS = "Horarios_Actividades.txt";
    private static final String ARCHIVO_ESTADOS = "ReservasEstado.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    // Listas para combobox
    private ObservableList<String> salas = FXCollections.observableArrayList();
    private ObservableList<String> clientes = FXCollections.observableArrayList();
    private ObservableList<String> horarios = FXCollections.observableArrayList();
    private ObservableList<String> estados = FXCollections.observableArrayList();

    // Estado
    private boolean modificando = false;

    @FXML
    public void initialize() {
        configurarListeners();
        cargarDatosCombobox();
    }

    private void configurarListeners() {
        idReserva.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                limpiarCampos(true);
                desactivarCampos();
            } else {
                validarIdReserva(newValue.trim());
            }
        });
    }

    private void cargarDatosCombobox() {
        cargarSalas();
        cargarClientes();
        cargarHorarios();
        cargarEstados();
    }

    private void validarIdReserva(String idReserva) {
        File archivo = new File(ARCHIVO_RESERVAS);

        if (!archivo.exists()) {
            Notificador.setText("Creando");
            activarCampos();
            fechaReserva.setValue(LocalDate.now());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            boolean encontrado = false;

            String linea;
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
            mostrarAlerta("Error al leer reservas", AlertType.ERROR);
        }
    }

    private void cargarDatosReserva(String[] datos) {
        if (datos.length >= 6) {
            idSalaReserva.setValue(datos[1]);
            idClienteReserva.setValue(datos[2]);
            fechaReserva.setValue(LocalDate.parse(datos[3], DATE_FORMATTER));
            idHorarioReserva.setValue(datos[4]);
            IdEstadoReserva.setValue(datos[5]);
        }
    }

    @FXML
    private void guardar(javafx.event.ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        // Validar que las referencias existan
        if (!salaExiste(idSalaReserva.getValue())) {
            mostrarAlerta("La sala seleccionada no existe", AlertType.ERROR);
            return;
        }

        if (!clienteExiste(idClienteReserva.getValue())) {
            mostrarAlerta("El cliente seleccionado no existe", AlertType.ERROR);
            return;
        }

        if (!horarioExiste(idHorarioReserva.getValue())) {
            mostrarAlerta("El horario seleccionado no existe", AlertType.ERROR);
            return;
        }

        if (!estadoExiste(IdEstadoReserva.getValue())) {
            mostrarAlerta("El estado seleccionado no existe", AlertType.ERROR);
            return;
        }

        String linea = crearLineaReserva();
        List<String> lineas = new ArrayList<>();

        try {
            lineas = actualizarArchivoReservas(linea);
            guardarArchivoReservas(lineas);

            mostrarAlerta("Reserva " + (modificando ? "modificada" : "creada") + " exitosamente", AlertType.INFORMATION);
            Notificador.setText(modificando ? "Modificado" : "Creado");

        } catch (IOException e) {
            mostrarAlerta("Error al guardar reserva", AlertType.ERROR);
        }
    }

    private List<String> actualizarArchivoReservas(String nuevaLinea) throws IOException {
        List<String> lineas = new ArrayList<>();
        File archivo = new File(ARCHIVO_RESERVAS);

        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String lineaActual;
                while ((lineaActual = br.readLine()) != null) {
                    if (!lineaActual.trim().isEmpty()) {
                        String[] partes = lineaActual.split(":");
                        if (partes.length > 0 && partes[0].equals(idReserva.getText().trim())) {
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_RESERVAS))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
        }
    }

    private String crearLineaReserva() {
        return String.join(":",
                idReserva.getText().trim(),
                idSalaReserva.getValue().trim(),
                idClienteReserva.getValue().trim(),
                fechaReserva.getValue().format(DATE_FORMATTER),
                idHorarioReserva.getValue().trim(),
                IdEstadoReserva.getValue().trim()
        );
    }

    // Métodos de validación de existencia
    private boolean salaExiste(String idSala) {
        return idSala != null && salas.contains(idSala);
    }

    private boolean clienteExiste(String idCliente) {
        return idCliente != null && clientes.contains(idCliente);
    }

    private boolean horarioExiste(String idHorario) {
        return idHorario != null && horarios.contains(idHorario);
    }

    private boolean estadoExiste(String idEstado) {
        return idEstado != null && estados.contains(idEstado);
    }

    private boolean validarCampos() {
        if (idReserva.getText().trim().isEmpty() ||
                idSalaReserva.getValue() == null ||
                idClienteReserva.getValue() == null ||
                fechaReserva.getValue() == null ||
                idHorarioReserva.getValue() == null ||
                IdEstadoReserva.getValue() == null) {

            mostrarAlerta("Todos los campos son obligatorios", AlertType.ERROR);
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
            idReserva.clear();
        }
        idSalaReserva.setValue(null);
        idClienteReserva.setValue(null);
        fechaReserva.setValue(null);
        idHorarioReserva.setValue(null);
        IdEstadoReserva.setValue(null);
        desactivarCampos();
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

    // Métodos para cargar datos en combobox
    private void cargarSalas() {
        cargarDatosArchivo(ARCHIVO_SALAS, salas, idSalaReserva, 1);
    }

    private void cargarClientes() {
        cargarDatosArchivo(ARCHIVO_CLIENTES, clientes, idClienteReserva, 0);
    }

    private void cargarHorarios() {
        cargarDatosArchivo(ARCHIVO_HORARIOS, horarios, idHorarioReserva, 0);
    }

    private void cargarEstados() {
        cargarDatosArchivo(ARCHIVO_ESTADOS, estados, IdEstadoReserva, 0);
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