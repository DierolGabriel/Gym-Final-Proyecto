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

public class SalasController {

    @FXML private TextField idSala;
    @FXML private TextField Nombre;
    @FXML private TextArea Descripcion;
    @FXML private ComboBox<String> IdLocalizacion;
    @FXML private TextField Notificador;
    @FXML private Button Guardar;
    @FXML private Button Limpiar;
    @FXML private Button Salir;

    private ObservableList<String> localizaciones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarListeners();
        cargarLocalizaciones();
    }

    private void configurarListeners() {
        idSala.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                limpiarCampos();
                return;
            }

            if (!newValue.matches("\\d*")) {
                idSala.setText(oldValue);
                return;
            }

            buscarSala(newValue);
        });
    }

    private void buscarSala(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader("Salas.txt"))) {
            boolean encontrado = false;
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length >= 4 && partes[1].equals(id)) {
                    cargarDatos(partes);
                    Notificador.setText("Modificando");
                    activarCampos();
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                activarCampos();
                Notificador.setText("Creando");
                limpiarDatosEdicion();
            }
        } catch (IOException e) {
            mostrarAlerta("Error al buscar sala", Alert.AlertType.ERROR);
        }
    }

    private void cargarLocalizaciones() {
        localizaciones.clear();
        File archivo = new File("Localización.txt");

        if (!archivo.exists()) {
            mostrarAlerta("Archivo de localizaciones no encontrado", Alert.AlertType.WARNING);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        localizaciones.add(partes[0].trim());
                    }
                }
            }
            IdLocalizacion.setItems(localizaciones);
        } catch (IOException e) {
            mostrarAlerta("Error al cargar localizaciones", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void Guardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        try {
            List<String> lineas = new ArrayList<>();
            boolean existeRegistro = false;
            int idSalaNum = Integer.parseInt(idSala.getText().trim());

            if (new File("Salas.txt").exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader("Salas.txt"))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        if (!linea.trim().isEmpty()) {
                            String[] partes = linea.split(":");
                            if (partes.length >= 2 && partes[1].equals(String.valueOf(idSalaNum))) {
                                lineas.add(crearLineaSala());
                                existeRegistro = true;
                            } else {
                                lineas.add(linea);
                            }
                        }
                    }
                }
            }

            if (!existeRegistro) {
                lineas.add(crearLineaSala());
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Salas.txt"))) {
                for (String linea : lineas) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            mostrarAlerta("Datos guardados exitosamente", Alert.AlertType.INFORMATION);
            limpiarCampos();
        } catch (NumberFormatException e) {
            mostrarAlerta("El ID de sala debe ser un número", Alert.AlertType.ERROR);
        } catch (IOException e) {
            mostrarAlerta("Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarDatos(String[] datos) {
        IdLocalizacion.setValue(datos[0]);
        Nombre.setText(datos[2]);
        Descripcion.setText(datos[3]);
    }

    private void activarCampos() {
        Nombre.setDisable(false);
        Descripcion.setDisable(false);
        IdLocalizacion.setDisable(false);
    }

    private void limpiarCampos() {
        idSala.setText("");
        Notificador.setText("");
        limpiarDatosEdicion();
        desactivarCamposEdicion();
    }

    private void limpiarDatosEdicion() {
        Nombre.setText("");
        Descripcion.setText("");
        IdLocalizacion.setValue(null);
    }

    private void desactivarCamposEdicion() {
        Nombre.setDisable(true);
        Descripcion.setDisable(true);
        IdLocalizacion.setDisable(true);
    }

    private boolean validarCampos() {
        if (IdLocalizacion.getValue() == null || idSala.getText().trim().isEmpty() ||
                Nombre.getText().trim().isEmpty() || Descripcion.getText().trim().isEmpty()) {
            mostrarAlerta("Todos los campos deben estar llenos", Alert.AlertType.ERROR);
            return false;
        }

        if (!existeLocalizacion(IdLocalizacion.getValue())) {
            mostrarAlerta("Seleccione una ID de Localización válida", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private boolean existeLocalizacion(String id) {
        if (id == null) return false;

        try (BufferedReader br = new BufferedReader(new FileReader("Localización.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith(id + ":")) {
                    return true;
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error validando localización", Alert.AlertType.ERROR);
        }
        return false;
    }

    private String crearLineaSala() {
        return String.join(":",
                IdLocalizacion.getValue(),
                idSala.getText().trim(),
                Nombre.getText().trim(),
                Descripcion.getText().trim()
        );
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void Salir(ActionEvent actionEvent)
    {
        ((Stage) Notificador.getScene().getWindow()).close();
    }

    public void Limpiar(ActionEvent actionEvent)
    {
        limpiarCampos();
        Notificador.setText("");
        IdLocalizacion.setValue(null);
        limpiarDatosEdicion();
        desactivarCamposEdicion();
    }
}