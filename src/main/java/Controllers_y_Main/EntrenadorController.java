package Controllers_y_Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorController {

    @FXML
    private TextField ApellidoEnt;

    @FXML
    private TextField CorreoEnt;

    @FXML
    private Button Guargar;

    @FXML
    private TextField IDEnt;

    @FXML
    private Button Limpiar;

    @FXML
    private TextField NombreEnt;

    @FXML
    private TextField Notificador;

    @FXML
    private Button Salir;

    @FXML
    private TextField TelefonoEnt;

    @FXML
    public void initialize() {
        configurarListenerID();
    }

    private void configurarListenerID() {
        IDEnt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (newValue.matches("\\d*")) {
                    buscarEntrenador(newValue);
                } else {
                    IDEnt.setText(oldValue);
                }
            } else {
                limpiarCampos();
                Notificador.setText("");
                desactivar();
            }
        });
    }

    private void buscarEntrenador(String id) {
        File archivo = new File("Entrenadores.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length >= 5 && partes[0].equals(id))
                {
                    cargarDatos(partes);
                    Notificador.setText("Modificando");
                    activar();
                    return;
                }
            }if (IDEnt.getText().isEmpty())
            {
                desactivar();
                limpiarCampos();
            }
            else
            {
                limpiarCampos();
                activar();
                Notificador.setText("Creando");
            }
        } catch (IOException e) {
            mostrarAlerta("Error al buscar");
        }
    }

    private void cargarDatos(String[] datos) {
        NombreEnt.setText(datos[1]);
        ApellidoEnt.setText(datos[2]);
        TelefonoEnt.setText(datos[3]);
        CorreoEnt.setText(datos[4]);
    }

    @FXML
    void Guardar(ActionEvent event)
    {

        if (!validarCamposCompletos())
        {
            mostrarAlerta("Todos los campos deben de esta llenos");
            return;
        }

        int idEntrenador;
        try {
            idEntrenador = Integer.parseInt(IDEnt.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("El id debe ser un numero entero");
            return;
        }

        File archivo = new File("Entrenadores.txt");
        List<String> lineas = new ArrayList<>();
        boolean existeEntrenador = false;

        try {
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        if (!linea.trim().isEmpty()) {
                            String[] partes = linea.split(":");
                            if (partes.length >= 1) {
                                try {
                                    int idActual = Integer.parseInt(partes[0].trim());
                                    if (idActual == idEntrenador) {
                                        lineas.add(crearLineaEntrenador());
                                        existeEntrenador = true;
                                        continue;
                                    }
                                } catch (NumberFormatException e) {
                                }
                            }
                            lineas.add(linea);
                        }
                    }
                }
            }

            if (!existeEntrenador) {
                lineas.add(crearLineaEntrenador());
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (String linea : lineas) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            mostrarAlerta("Datos guardados exitosamente");

            limpiarFormulario();

        } catch (IOException e) {
            mostrarAlerta("Error al guardar el entrenador");
        }
    }//fin del guardar

    private boolean validarCamposCompletos() {
        return !IDEnt.getText().trim().isEmpty() &&
                !NombreEnt.getText().trim().isEmpty() &&
                !ApellidoEnt.getText().trim().isEmpty() &&
                !TelefonoEnt.getText().trim().isEmpty() &&
                !CorreoEnt.getText().trim().isEmpty();
    }

    private String crearLineaEntrenador() {
        return String.join(":",
                IDEnt.getText().trim(),
                NombreEnt.getText().trim(),
                ApellidoEnt.getText().trim(),
                TelefonoEnt.getText().trim(),
                CorreoEnt.getText().trim()
        );
    }

    private void limpiarFormulario() {
        IDEnt.setText("");
        NombreEnt.setText("");
        ApellidoEnt.setText("");
        TelefonoEnt.setText("");
        CorreoEnt.setText("");
        Notificador.setText("");
        desactivar();
    }

    private void limpiarCampos() {
        NombreEnt.setText("");
        ApellidoEnt.setText("");
        TelefonoEnt.setText("");
        CorreoEnt.setText("");
        Notificador.setText("");
    }

    @FXML
    void Limpiar(ActionEvent event)
    {
        IDEnt.setText("");
        ApellidoEnt.setText("");
        CorreoEnt.setText("");
        NombreEnt.setText("");
        TelefonoEnt.setText("");
        Notificador.setText("");

        ApellidoEnt.setEditable(false);
        CorreoEnt.setEditable(false);
        NombreEnt.setEditable(false);
        TelefonoEnt.setEditable(false);

        ApellidoEnt.setDisable(true);
        CorreoEnt.setDisable(true);
        NombreEnt.setDisable(true);
        TelefonoEnt.setDisable(true);
    }

    @FXML
    void Salir(ActionEvent event)
    {
        Stage stageActual = (Stage) IDEnt.getScene().getWindow();
        stageActual.close();
    }

    public void activar()
    {
        ApellidoEnt.setEditable(true);
        CorreoEnt.setEditable(true);
        NombreEnt.setEditable(true);
        TelefonoEnt.setEditable(true);

        ApellidoEnt.setDisable(false);
        CorreoEnt.setDisable(false);
        NombreEnt.setDisable(false);
        TelefonoEnt.setDisable(false);
    }

    public void desactivar()
    {
        ApellidoEnt.setEditable(false);
        CorreoEnt.setEditable(false);
        NombreEnt.setEditable(false);
        TelefonoEnt.setEditable(false);

        ApellidoEnt.setDisable(true);
        CorreoEnt.setDisable(true);
        NombreEnt.setDisable(true);
        TelefonoEnt.setDisable(true);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

