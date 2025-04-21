package Controllers_y_Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class HorariosController {

    // Componentes FXML
    @FXML private DatePicker DiaAct;
    @FXML private Button Guardar;
    @FXML private TextField HoraAct;
    @FXML private ComboBox<String> ComboActividades;
    @FXML private TextField IdHorarioActividad;
    @FXML private Button Limpiar;
    @FXML private TextField Notificador;
    @FXML private Button Salir;

    // Constantes
    private static final String ARCHIVO_HORARIOS = "Horarios_Actividades.txt";
    private static final String ARCHIVO_ACTIVIDADES = "Actividades.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String PATRON_HORA = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";

    // Estado
    private boolean modificando = false;
    private ObservableList<String> actividadesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarListeners();
        cargarActividades();
    }

    private void configurarListeners() {
        IdHorarioActividad.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                limpiarCampos(false);
                Notificador.clear();
                modificando = false;
            } else {
                validarIdHorario(newValue.trim());
            }
        });
    }

    private void cargarActividades() {
        actividadesList.clear();
        File archivo = new File(ARCHIVO_ACTIVIDADES);

        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (!linea.trim().isEmpty()) {
                        String[] partes = linea.split(":");
                        if (partes.length > 0) {
                            actividadesList.add(partes[0].trim());
                        }
                    }
                }
                ComboActividades.setItems(actividadesList);
            } catch (IOException e) {
                mostrarAlerta("Error al cargar actividades", AlertType.ERROR);
            }
        }
    }

    private void validarIdHorario(String idHorario) {
        File archivo = new File(ARCHIVO_HORARIOS);

        if (!archivo.exists()) {
            Notificador.setText("Creando");
            activarCampos();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            boolean encontrado = false;

            String linea;
            while ((linea = br.readLine()) != null && !linea.isEmpty()) {
                String[] partes = linea.split(":");
                if (partes.length >= 5 && partes[0].equals(idHorario)) {
                    cargarDatosHorario(partes);
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
                modificando = false;
            }

        } catch (IOException e) {
            mostrarAlerta("Error al leer horarios", AlertType.ERROR);
        }
    }

    private void cargarDatosHorario(String[] datos) {
        if (datos.length >= 5) {
            try {
                LocalDate fecha = LocalDate.parse(datos[1], DATE_FORMATTER);
                DiaAct.setValue(fecha);
                HoraAct.setText(datos[2] + ":" + datos[3]);
                ComboActividades.setValue(datos[4]);
            } catch (DateTimeParseException e) {
                mostrarAlerta("Fecha inválida en registro", AlertType.ERROR);
            }
        }
    }

    @FXML
    private void guardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        String idActividad = ComboActividades.getValue();
        if (!actividadExiste(idActividad)) {
            mostrarAlerta("La actividad seleccionada no existe", AlertType.ERROR);
            return;
        }

        String idHorario = IdHorarioActividad.getText().trim();
        String dia = DiaAct.getValue().format(DATE_FORMATTER);
        String hora = HoraAct.getText().trim();
        String linea = String.join(":", idHorario, dia, hora.split(":")[0], hora.split(":")[1], idActividad);

        try {
            List<String> lineas = actualizarArchivoHorarios(idHorario, linea);
            guardarArchivoHorarios(lineas);

            mostrarAlerta("Horario " + (modificando ? "modificado" : "creado") + " exitosamente", AlertType.INFORMATION);
            Notificador.setText(modificando ? "Modificado" : "Creado");
            desactivarCampos();
            limpiarCampos(true);

        } catch (IOException e) {
            mostrarAlerta("Error al guardar horario", AlertType.ERROR);
        }
    }

    private List<String> actualizarArchivoHorarios(String idHorario, String nuevaLinea) throws IOException {
        List<String> lineas = new ArrayList<>();
        File archivo = new File(ARCHIVO_HORARIOS);

        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String lineaActual;
                while ((lineaActual = br.readLine()) != null) {
                    if (!lineaActual.trim().isEmpty()) {
                        String[] partes = lineaActual.split(":");
                        if (partes.length > 0 && partes[0].equals(idHorario)) {
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

    private void guardarArchivoHorarios(List<String> lineas) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_HORARIOS))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
        }
    }

    private boolean actividadExiste(String idActividad) {
        if (idActividad == null) return false;
        return actividadesList.contains(idActividad);
    }

    private boolean validarCampos() {
        if (IdHorarioActividad.getText().trim().isEmpty()) {
            mostrarAlerta("El ID de horario es obligatorio", AlertType.ERROR);
            return false;
        }

        if (DiaAct.getValue() == null) {
            mostrarAlerta("La fecha es obligatoria", AlertType.ERROR);
            return false;
        }

        if (HoraAct.getText().trim().isEmpty()) {
            mostrarAlerta("La hora es obligatoria", AlertType.ERROR);
            return false;
        }

        if (!HoraAct.getText().trim().matches(PATRON_HORA)) {
            mostrarAlerta("Formato de hora inválido (HH:MM)", AlertType.ERROR);
            return false;
        }

        if (ComboActividades.getValue() == null) {
            mostrarAlerta("Debe seleccionar una actividad", AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    private void limpiar(ActionEvent event) {
        limpiarCampos(true);
        Notificador.clear();
        modificando = false;
    }

    private void limpiarCampos(boolean limpiarId) {
        if (limpiarId) {
            IdHorarioActividad.clear();
        }
        DiaAct.setValue(null);
        HoraAct.clear();
        ComboActividades.setValue(null);
        desactivarCampos();
    }

    private void activarCampos() {
        DiaAct.setDisable(false);
        HoraAct.setDisable(false);
        ComboActividades.setDisable(false);
    }

    private void desactivarCampos() {
        DiaAct.setDisable(true);
        HoraAct.setDisable(true);
        ComboActividades.setDisable(true);
    }

    private void mostrarAlerta(String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == AlertType.ERROR ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void Salir(ActionEvent actionEvent)
    {
        ((Stage) Notificador.getScene().getWindow()).close();
    }
}