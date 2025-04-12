package Controllers_y_Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.*;
import java.time.chrono.Chronology;
import java.util.ArrayList;
import java.util.List;

public class HorariosController {

    @FXML
    private DatePicker DiaAct;


    @FXML
    private Button Guardar;

    @FXML
    private TextField HoraAct;

    @FXML
    private TextField IdActividad;

    @FXML
    private TextField IdHorarioActividad;

    @FXML
    private Button Limpiar;

    @FXML
    private TextField Notificador;

    @FXML
    private Button Salir;

    private static final String ARCHIVO_HORARIOS = "Horarios_Actividades.txt";
    private static final String ARCHIVO_ACTIVIDADES = "Actividades.txt";
    private boolean modificando = false;

    @FXML
    public void initialize() {
    }

    //TODO:SI STATUS FALSO =MODIFIVACAR TRUE=NO MODIFICAR
//TRUE MENSAJE QUE EL CLIENTE NO TIENE VALANCE PENDIENTE

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

    private boolean validarActividad(String idActividad) {
        File archivo = new File(ARCHIVO_ACTIVIDADES);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de actividades no existe");
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = br.readLine()) != null && !linea.isEmpty()) {
                String[] partes = linea.split(":");
                if (partes.length >= 1 && partes[0].equals(idActividad)) {
                    return true;
                }
            }

        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de actividades: " + e.getMessage());
        }

        return false;
    }

    private void cargarDatosHorario(String[] datos) {
        if (datos.length >= 4) {
            DiaAct.setChronology(Chronology.of(datos[1]));
            HoraAct.setText(datos[2]);
            IdActividad.setText(datos[3]);
        }
    }

    @FXML
    void Guardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        // Validar que la actividad exista (solo al guardar)
        if (!validarActividad(IdActividad.getText().trim())) {
            mostrarAlerta("No existe una actividad con ese ID");
            return;
        }

        String idHorario = IdHorarioActividad.getText().trim();
        Chronology dia = DiaAct.getChronology();
        String hora = HoraAct.getText().trim();
        String idActividad = IdActividad.getText().trim();

        String linea = String.join(":", idHorario, dia.getCalendarType(), hora, idActividad);

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

    private void limpiarCampos(boolean incluirId) {
        if (incluirId) {
            IdHorarioActividad.clear();
        }
        DiaAct.setValue(null);
        HoraAct.clear();
        IdActividad.clear();
        desactivarCampos();
    }

    private boolean validarCampos() {
        if (IdHorarioActividad.getText().trim().isEmpty() ||
                DiaAct.getChronology() == null ||
                HoraAct.getText().trim().isEmpty() ||
                IdActividad.getText().trim().isEmpty()) {

            mostrarAlerta("Todos los campos son obligatorios");
            return false;
        }

        // Validar formato de hora (HH:MM)
        if (!HoraAct.getText().trim().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            mostrarAlerta("Formato de hora inválido. Use HH:MM");
            return false;
        }

        return true;
    }

    private void activarCampos() {
        DiaAct.setDisable(false);
        HoraAct.setDisable(false);
        IdActividad.setDisable(false);
    }

    private void desactivarCampos() {
        DiaAct.setDisable(true);
        HoraAct.setDisable(true);
        IdActividad.setDisable(true);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}