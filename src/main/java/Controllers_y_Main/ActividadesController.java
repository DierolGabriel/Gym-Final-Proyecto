package Controllers_y_Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ActividadesController {

    // Componentes FXML
    @FXML private TextField txtIdAct;
    @FXML private TextField txtNombreAct;
    @FXML private TextField txtDescripcionAct;
    @FXML private ComboBox<String> txtIdEntrenadorAct;
    @FXML private ComboBox<String> txtIdLocalizacionAct;
    @FXML private TextField lblEstado;
    @FXML private Button Limpiar;
    @FXML private Button Salir;

    // Archivos
    private final String ARCHIVO_ACTIVIDADES = "Actividades.txt";
    private final String ARCHIVO_LOCALIZACIONES = "Localización.txt";
    private final String ARCHIVO_ENTRENADORES = "Entrenadores.txt";

    // Listas para combobox
    private ObservableList<String> localizaciones = FXCollections.observableArrayList();
    private ObservableList<String> entrenadores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        crearArchivosSiNoExisten();
        configurarListeners();
        cargarDatosCombobox();
        desactivarCampos();
    }

    private void crearArchivosSiNoExisten() {
        crearArchivo(ARCHIVO_ACTIVIDADES);
        crearArchivo(ARCHIVO_LOCALIZACIONES);
        crearArchivo(ARCHIVO_ENTRENADORES);
    }

    private void crearArchivo(String rutaArchivo) {
        try {
            new File(rutaArchivo).createNewFile();
        } catch (IOException e) {
            mostrarAlerta("Error al crear " + rutaArchivo, Alert.AlertType.ERROR);
        }
    }

    private void configurarListeners() {
        txtIdAct.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                limpiarYDesactivar();
                return;
            }
            buscarActividad(newValue.trim());
        });
    }

    private void cargarDatosCombobox() {
        cargarDatosArchivo(ARCHIVO_ENTRENADORES, entrenadores, txtIdEntrenadorAct);
        cargarDatosArchivo(ARCHIVO_LOCALIZACIONES, localizaciones, txtIdLocalizacionAct);
    }

    private void cargarDatosArchivo(String archivo, ObservableList<String> lista, ComboBox<String> comboBox) {
        lista.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        lista.add(partes[0].trim());
                    }
                }
            }
            comboBox.setItems(lista);
        } catch (IOException e) {
            mostrarAlerta("Error al cargar " + archivo, Alert.AlertType.ERROR);
        }
    }

    private void buscarActividad(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_ACTIVIDADES))) {
            boolean encontrado = false;
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(":");
                if (datos.length >= 5 && datos[0].equals(id)) {
                    cargarDatosActividad(datos);
                    lblEstado.setText("Modificando");
                    activarCampos();
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                limpiarCamposEdicion();
                lblEstado.setText("Creando");
                activarCampos();
            }
        } catch (IOException e) {
            mostrarAlerta("Error al buscar actividad", Alert.AlertType.ERROR);
        }
    }

    private void cargarDatosActividad(String[] datos) {
        txtNombreAct.setText(datos[1]);
        txtDescripcionAct.setText(datos[2]);
        txtIdLocalizacionAct.setValue(datos[3]);
        txtIdEntrenadorAct.setValue(datos[4]);
    }

    @FXML
    private void guardarActividad() {
        if (!validarCampos()) {
            return;
        }

        // Validar que los IDs de los ComboBox existan
        if (!idLocalizacionExiste(txtIdLocalizacionAct.getValue())) {
            mostrarAlerta("La localización seleccionada no existe", Alert.AlertType.ERROR);
            return;
        }

        if (!idEntrenadorExiste(txtIdEntrenadorAct.getValue())) {
            mostrarAlerta("El entrenador seleccionado no existe", Alert.AlertType.ERROR);
            return;
        }

        String lineaNueva = crearLineaActividad();
        List<String> lineas = new ArrayList<>();
        boolean actualizado = false;

        try {
            // Leer y actualizar registros existentes
            if (new File(ARCHIVO_ACTIVIDADES).exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_ACTIVIDADES))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        if (!linea.trim().isEmpty()) {
                            String[] datos = linea.split(":");
                            if (datos.length > 0 && datos[0].equals(txtIdAct.getText().trim())) {
                                lineas.add(lineaNueva);
                                actualizado = true;
                            } else {
                                lineas.add(linea);
                            }
                        }
                    }
                }
            }

            // Agregar nuevo registro si no existía
            if (!actualizado) {
                lineas.add(lineaNueva);
            }

            // Escribir todos los registros
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ACTIVIDADES))) {
                for (String linea : lineas) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            mostrarAlerta("Actividad guardada correctamente", Alert.AlertType.INFORMATION);
            limpiarYDesactivar();
        } catch (IOException e) {
            mostrarAlerta("Error al guardar actividad", Alert.AlertType.ERROR);
        }
    }

    private boolean idLocalizacionExiste(String idLocalizacion) {
        if (idLocalizacion == null) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_LOCALIZACIONES))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length > 0 && partes[0].trim().equals(idLocalizacion)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al validar localización", Alert.AlertType.ERROR);
        }
        return false;
    }

    private boolean idEntrenadorExiste(String idEntrenador) {
        if (idEntrenador == null) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_ENTRENADORES))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length > 0 && partes[0].trim().equals(idEntrenador)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al validar entrenador", Alert.AlertType.ERROR);
        }
        return false;
    }


    private String crearLineaActividad() {
        return String.join(":",
                txtIdAct.getText().trim(),
                txtNombreAct.getText().trim(),
                txtDescripcionAct.getText().trim(),
                txtIdLocalizacionAct.getValue().trim(),
                txtIdEntrenadorAct.getValue().trim()
        );
    }

    private boolean validarCampos() {
        if (txtIdAct.getText().trim().isEmpty() ||
                txtNombreAct.getText().trim().isEmpty() ||
                txtDescripcionAct.getText().trim().isEmpty() ||
                txtIdLocalizacionAct.getValue() == null ||
                txtIdEntrenadorAct.getValue() == null) {

            mostrarAlerta("Todos los campos son obligatorios", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }



    private void limpiarYDesactivar() {
        txtIdAct.clear();
        limpiarCamposEdicion();
        desactivarCampos();
        lblEstado.setText("");
    }

    private void limpiarCamposEdicion() {
        txtNombreAct.clear();
        txtDescripcionAct.clear();
        txtIdLocalizacionAct.setValue(null);
        txtIdEntrenadorAct.setValue(null);
    }

    private void activarCampos() {
        txtNombreAct.setDisable(false);
        txtDescripcionAct.setDisable(false);
        txtIdLocalizacionAct.setDisable(false);
        txtIdEntrenadorAct.setDisable(false);
    }

    private void desactivarCampos() {
        txtNombreAct.setDisable(true);
        txtDescripcionAct.setDisable(true);
        txtIdLocalizacionAct.setDisable(true);
        txtIdEntrenadorAct.setDisable(true);
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void Salir(ActionEvent actionEvent) {
        ((Stage) txtIdAct.getScene().getWindow()).close();
    }

    public void Limpiar(ActionEvent actionEvent) {
        limpiarYDesactivar();
    }
}