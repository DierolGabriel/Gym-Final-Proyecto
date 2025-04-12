package Controllers_y_Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SalasController {

    @FXML private Button Limpiar;
    @FXML private Button Salir;
    @FXML private TextArea Descripcion;
    @FXML private Button Guardar;
    @FXML private ComboBox<String> IdLocalizacion;
    @FXML private TextField Nombre;
    @FXML private TextField Notificador;
    @FXML private TextField idSala;

    @FXML
    public void initialize() {
        configurarListenerID();
        cargarLocalizaciones();
    }

    private void configurarListenerID() {
        idSala.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (newValue.matches("\\d*"))
                {
                    buscarLocalizacion(newValue);
                } else {
                    idSala.setText(oldValue);
                }
            } else {
                limpiarCampos();
                Notificador.setText("");
                Descripcion.setText("");
                IdLocalizacion.setValue(null);
                Nombre.setText("");
                Descripcion.setDisable(true);
                Nombre.setDisable(true);
                IdLocalizacion.setDisable(true);
            }
        });
    }

    private void buscarLocalizacion(String id) {
        File archivo = new File("Salas.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length >= 4 && partes[1].equals(id))
                {
                    cargarDatos(partes);
                    Notificador.setText("Modificando");
                    activar();
                    return;
                }
            }if (idSala.getText().isEmpty())
            {
            limpiarCampos();
            }
            else
            {
                activar();
                Notificador.setText("Creando");
                Descripcion.setText("");
                IdLocalizacion.setValue(null);
                Nombre.setText("");
            }
        } catch (IOException e) {
            mostrarAlerta("Error al buscar");
        }
    }

    private void cargarDatos(String[] datos)
    {
        IdLocalizacion.setValue(datos[0]);
        Nombre.setText(datos[2]);
        Descripcion.setText(datos[3]);
    }

    private void cargarLocalizaciones() {
        File archivo = new File("Localización.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            IdLocalizacion.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        IdLocalizacion.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar localizaciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void Guardar(ActionEvent event) {
        if (!validarCamposCompletos()) {
            mostrarAlerta("Todos los campos deben estar llenos", Alert.AlertType.ERROR);
            return;
        }

        if (!existeLocalizacion(IdLocalizacion.getValue())) {
            mostrarAlerta("Selecione una id de Localización", Alert.AlertType.ERROR);
            return;
        }

        int idSalaNum;
        try {
            idSalaNum = Integer.parseInt(idSala.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("El ID de sala debe ser un número entero", Alert.AlertType.ERROR);
            return;
        }

        File archivo = new File("Salas.txt");
        List<String> lineas = new ArrayList<>();
        boolean existeRegistro = false;

        try {
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        if (!linea.trim().isEmpty()) {
                            String[] partes = linea.split(":");
                            if (partes.length >= 2) {
                                try {
                                    int idActual = Integer.parseInt(partes[0].trim());
                                    if (idActual == idSalaNum)
                                    {
                                        lineas.add(crearLineaSala());
                                        existeRegistro = true;
                                        continue;
                                    }
                                } catch (NumberFormatException ignored) {}
                            }
                            lineas.add(linea);
                        }
                    }
                }
            }

            if (!existeRegistro) {
                lineas.add(crearLineaSala());
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (String linea : lineas) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            mostrarAlerta("Datos guardados exitosamente", Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (IOException e) {
            mostrarAlerta("Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean existeLocalizacion(String id) {
        File archivo = new File("Localización.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith(id + ":")) return true;
            }
        } catch (IOException e) {
            mostrarAlerta("Error validando localización", Alert.AlertType.ERROR);
        }
        return false;
    }

    private boolean validarCamposCompletos() {
        return IdLocalizacion.getValue() != null &&
                !idSala.getText().trim().isEmpty() &&
                !Nombre.getText().trim().isEmpty() &&
                !Descripcion.getText().trim().isEmpty();
    }

    private String crearLineaSala() {
        return String.join(":",
                IdLocalizacion.getValue(),
                idSala.getText().trim(),
                Nombre.getText().trim(),
                Descripcion.getText().trim()
        );
    }

    private void cargarDatosSala(String[] partes) {
        Nombre.setText(partes[2]);
        Descripcion.setText(partes[3]);
        IdLocalizacion.setValue(partes[0]);
        Notificador.setText("Modificando");
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String crearLineaEntrenador()
    {
        return String.join(":",
                IdLocalizacion.getValue().trim(),
                idSala.getText().trim(),
                Nombre.getText().trim(),
                Descripcion.getText().trim()
        );
    }

    @FXML
    void Limpiar(ActionEvent event)
    {
        Descripcion.setText("");
        IdLocalizacion.setValue("");
        Nombre.setText("");
        Notificador.setText("");
        idSala.setText("");

        Nombre.setDisable(true);
        Descripcion.setDisable(true);
        IdLocalizacion.setDisable(true);
    }

    @FXML
    void Salir(ActionEvent event)
    {
        Stage stageActual = (Stage) idSala.getScene().getWindow();
        stageActual.close();
    }
    public void activar()
    {
        Nombre.setDisable(false);
        Descripcion.setDisable(false);
        IdLocalizacion.setDisable(false);
    }

    private void limpiarCampos()
    {
        idSala.setText("");
        Notificador.setText("");
        Descripcion.setText("");
        IdLocalizacion.setValue(null);
        Nombre.setText("");
        Descripcion.setDisable(true);
        Nombre.setDisable(true);
        IdLocalizacion.setDisable(true);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
