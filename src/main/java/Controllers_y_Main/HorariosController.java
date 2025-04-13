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
import java.util.ArrayList;
import java.util.List;

public class HorariosController {

    @FXML private DatePicker DiaAct;
    @FXML private Button Guardar;
    @FXML private TextField HoraAct;
    @FXML private ComboBox<String> ComboActividades;
    @FXML private TextField IdHorarioActividad;
    @FXML private Button Limpiar;
    @FXML private TextField Notificador;
    @FXML private Button Salir;

    private static final String ARCHIVO_HORARIOS = "Horarios_Actividades.txt";
    private static final String ARCHIVO_ACTIVIDADES = "Actividades.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private boolean modificando = false;

    @FXML
    public void initialize() {
        IdHorarioActividad.textProperty().addListener((observable, oldValue, newValue) -> {
            validarIdHorarioOnChange();
        });

        cargarActividades();
    }

    private void cargarActividades() {
        ObservableList<String> actividades = FXCollections.observableArrayList();
        File archivo = new File(ARCHIVO_ACTIVIDADES);

        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (!linea.trim().isEmpty()) {
                        String[] partes = linea.split(":");
                        if (partes.length > 0) {
                            actividades.add(partes[0]);
                        }
                    }
                }
                ComboActividades.setItems(actividades);
            } catch (IOException e) {
                mostrarAlerta("Error al cargar actividades: " + e.getMessage());
            }
        }
    }

    @FXML
    void validarIdHorarioOnChange() {
        String idHorario = IdHorarioActividad.getText().trim();
        if (!idHorario.isEmpty()) {
            validarIdHorario(idHorario);
        } else {
            limpiarCampos(false);
            Notificador.clear();
            modificando = false;
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
            String linea;
            boolean encontrado = false;

            while ((linea = br.readLine()) != null && !linea.isEmpty()) {
                String[] partes = linea.split(":");
                if (partes.length >= 4 && partes[0].equals(idHorario)) {
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
            mostrarAlerta("Error al leer el archivo de horarios: " + e.getMessage());
        }
    }

    private void cargarDatosHorario(String[] datos) {
        if (datos.length >= 4) {
            try {
                LocalDate fecha = LocalDate.parse(datos[1], DATE_FORMATTER);
                DiaAct.setValue(fecha);
                HoraAct.setText(datos[2]+":"+datos[3]);
                ComboActividades.setValue(datos[4]);
            } catch (Exception e) {
                mostrarAlerta("Formato de fecha inválido en el archivo");
            }
        }
    }

    @FXML
    void Guardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        String idActividad = ComboActividades.getValue();
        if (idActividad == null || idActividad.isEmpty()) {
            mostrarAlerta("Debe seleccionar una actividad");
            return;
        }

        String idHorario = IdHorarioActividad.getText().trim();
        String dia = DiaAct.getValue() != null ? DiaAct.getValue().format(DATE_FORMATTER) : "";
        String hora = HoraAct.getText().trim();

        String linea = String.join(":", idHorario, dia, hora, idActividad);

        File archivo = new File(ARCHIVO_HORARIOS);
        List<String> lineas = new ArrayList<>();
        boolean existe = false;

        try {
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String lineaActual;
                    while ((lineaActual = br.readLine()) != null) {
                        if (!lineaActual.trim().isEmpty()) {
                            String[] partes = lineaActual.split(":");
                            if (partes.length > 0 && partes[0].equals(idHorario)) {
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

            mostrarAlerta("Horario " + (existe ? "modificado" : "creado") + " exitosamente");
            Notificador.setText(existe ? "Modificado" : "Creado");

        } catch (IOException e) {
            mostrarAlerta("Error al guardar el horario: " + e.getMessage());
        }
    }

    @FXML
    void Limpiar(ActionEvent event) {
        limpiarCampos(true);
        Notificador.clear();
        modificando = false;
    }

    @FXML
    void Salir(ActionEvent event) {
        Stage stageActual = (Stage) Notificador.getScene().getWindow();
        stageActual.close();
    }

    private boolean validarCampos() {
        if (IdHorarioActividad.getText().trim().isEmpty() ||
                DiaAct.getValue() == null ||
                HoraAct.getText().trim().isEmpty() ||
                ComboActividades.getValue() == null) {

            mostrarAlerta("Todos los campos son obligatorios");
            return false;
        }

        if (!HoraAct.getText().trim().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            mostrarAlerta("Formato de hora inválido. Use HH:MM");
            return false;
        }

        return true;
    }

    private void limpiarCampos(boolean incluirId) {
        if (incluirId) {
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

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}