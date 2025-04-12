package Controllers_y_Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoRController {

    @FXML
    private CheckBox Estado;

    @FXML
    private Button Guardar;

    @FXML
    private Button Limpiar;

    @FXML
    private TextField Notificador;

    @FXML
    private Button Salir;

    @FXML
    private TextField idReserva;

    @FXML
    public void initialize() {
        configurarListenerID();
    }

    private void configurarListenerID() {
        idReserva.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (newValue.matches("\\d*")) {
                    buscarEntrenador(newValue);
                } else {
                    idReserva.setText(oldValue);
                }
            } else {
                Notificador.setText("");
                Estado.setDisable(true);
                Estado.setSelected(false);
                Estado.setSelected(false);
                Estado.setDisable(true);
            }
        });
    }

    private void buscarEntrenador(String id) {
        File archivo = new File("ReservasEstado.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length >= 2 && partes[0].equals(id))
                {
                    cargarDatos(partes);
                    Notificador.setText("Modificando");
                    activar();
                    return;
                }
            }if (idReserva.getText().isEmpty())
            {
                Notificador.setText("");
                Estado.setDisable(true);
                Estado.setSelected(false);
            }
            else
            {
                Notificador.setText("");
                Estado.setDisable(true);
                Estado.setSelected(false);
                activar();
                Notificador.setText("Creando");
            }
        } catch (IOException e) {
            mostrarAlerta("Error al buscar");
        }
    }

    private void cargarDatos(String[] datos)
    {
        idReserva.setText(datos[0]);
        if (datos[1].equals("1"))
        {
            Estado.setSelected(true);
        }
        else
        {
            Estado.setSelected(false);
        }
    }

    @FXML
    void Guardar(ActionEvent event)
    {
        if (!validarCamposCompletos())
        {
            mostrarAlerta("Todos los campos deben estar llenos");
            return;
        }

        int idLocal;
        try {
            idLocal = Integer.parseInt(idReserva.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("El ID debe ser un entero");
            return;
        }

        File archivo = new File("ReservasEstado.txt");
        List<String> lineas = new ArrayList<>();
        boolean existeLocal = false;

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
                                    boolean Disponible = Integer.parseInt(partes[1].trim()) == 1;

                                    if (idActual == idLocal)
                                    {
                                        lineas.add(crearLineaEntrenador());
                                        existeLocal = true;
                                        continue;
                                    }
                                } catch (NumberFormatException e) {
                                }
                            }
                            lineas.add(linea);
                        }
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!existeLocal)
            {
                lineas.add(crearLineaEntrenador());
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo)))
            {
                for (String linea : lineas)
                {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            mostrarAlerta("Datos guardados exitosamente");
            limpiarCampos();

        } catch (IOException e) {
            mostrarAlerta("Error al guardar el estado de reserva.");
        }
    }//fin del guardar

    private boolean validarCamposCompletos()
    {
        return !idReserva.getText().trim().isEmpty();
    }

    private String crearLineaEntrenador()
    {
        String Disponible = "";
        if (Estado.isSelected())
        {
            Disponible = "1";
        }
        else
        {
            Disponible = "0";
        }
        return String.join(":",
                idReserva.getText().trim()
                , Disponible.trim()
        );
    }

    @FXML
    void Limpiar(ActionEvent event)
    {
        idReserva.setText("");
        Notificador.setText("");
        Estado.setDisable(true);
        Estado.setSelected(false);
    }//fin del limpiar

    private void limpiarCampos()
    {
        Notificador.setText("");
        Estado.setDisable(true);
        Estado.setSelected(false);
        idReserva.setText("");
    }


    @FXML
    void Salir(ActionEvent event)
    {
        Stage stageActual = (Stage) idReserva.getScene().getWindow();
        stageActual.close();
    }

    public void activar()
    {
        Estado.setDisable(false);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}

