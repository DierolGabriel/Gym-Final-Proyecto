package Controllers_y_Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservaController {

    @FXML private TextField idReserva;
    @FXML private ComboBox idSalaReserva;
    @FXML private ComboBox idClienteReserva;
    @FXML private DatePicker fechaReserva;
    @FXML private ComboBox idHorarioReserva;
    @FXML private ComboBox IdEstadoReserva;
    @FXML private TextField Notificador;
    @FXML private Button Guardar;
    @FXML private Button Limpiar;
    @FXML private Button Salir;

    private static final String ARCHIVO_RESERVAS = "Reserva.txt";
    private boolean modificando = false;

    @FXML
    public void initialize() {
        idReserva.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                validarIdReserva(newValue);
            }
            else
            {
                limpiarCampos(true);
                desactivarCampos();
            }
        });
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
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean encontrado = false;

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
            mostrarAlerta("Error al leer el archivo de reservas: " + e.getMessage());
        }
    }

    private void cargarDatosReserva(String[] datos) {
        if (datos.length >= 6) {
            idSalaReserva.setValue(datos[1]);
            idClienteReserva.setValue(datos[2]);
            fechaReserva.setValue(LocalDate.parse(datos[3], DateTimeFormatter.ofPattern("d/M/yyyy")));
            idHorarioReserva.setValue(datos[4]);
            IdEstadoReserva.setValue(datos[5]);
        }
    }

    @FXML
    void Guardar(javafx.event.ActionEvent event)
    {
        if (!validarCampos()) {
            return;
        }

        String id = idReserva.getText().trim();
        String idSala = idSalaReserva.getValue().toString().trim();
        String idCliente = idClienteReserva.getValue().toString().trim();
        String fecha = fechaReserva.getValue().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        String idHorario = idHorarioReserva.getValue().toString().trim();
        String idEstado = IdEstadoReserva.getValue().toString().trim();

        String linea = String.join(":", id, idSala, idCliente, fecha, idHorario, idEstado);

        File archivo = new File(ARCHIVO_RESERVAS);
        List<String> lineas = new ArrayList<>();
        boolean existe = false;

        try {
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String lineaActual;
                    while ((lineaActual = br.readLine()) != null) {
                        if (!lineaActual.trim().isEmpty()) {
                            String[] partes = lineaActual.split(":");
                            if (partes.length > 0 && partes[0].equals(id)) {
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

            mostrarAlerta("Reserva " + (existe ? "modificada" : "creada") + " exitosamente");
            Notificador.setText(existe ? "Modificado" : "Creado");

        } catch (IOException e) {
            mostrarAlerta("Error al guardar la reserva: " + e.getMessage());
        }
    }

    private boolean existeEnArchivo(String id, String archivoRuta) {
        File archivo = new File(archivoRuta);
        if (!archivo.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length > 0 && partes[0].equals(id)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo: " + archivoRuta);
        }
        return false;
    }

    @FXML
    void Limpiar(javafx.event.ActionEvent event)
    {
        limpiarCampos(true);
        Notificador.clear();
        modificando = false;
    }

    @FXML
    void Salir(javafx.event.ActionEvent event)
    {
        Stage stageActual = (Stage) Notificador.getScene().getWindow();
        stageActual.close();
    }

    private void limpiarCampos(boolean incluirId) {
        if (incluirId) {
            idReserva.clear();
        }
        idSalaReserva.setValue(null);
        idClienteReserva.setValue(null);
        fechaReserva.setValue(null);
        idHorarioReserva.setValue(null);
        IdEstadoReserva.setValue(null);
        desactivarCampos();
    }

    private boolean validarCampos()
    {
        if (idReserva.getText() == null ||
                idSalaReserva.getValue() == null ||
                idClienteReserva.getValue() == null ||
                fechaReserva.getValue() == null ||
                idHorarioReserva.getValue() == null||
                IdEstadoReserva.getValue() == null)
        {
            mostrarAlerta("Todos los campos son obligatorios");
            return false;
        }

        return true;
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

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarSalas() {
        File archivo = new File("Salas.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            idSalaReserva.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        idSalaReserva.getItems().add(partes[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Salas: " + e.getMessage());
        }
    }//Fin de cargarSalas

    private void cargarClientes() {
        File archivo = new File("Clientes.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            idClienteReserva.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        idClienteReserva.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Clientes: " + e.getMessage());
        }
    }//Fin de cargarClientes

    private void cargarHorarios() {
        File archivo = new File("Horarios_Actividades.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            idHorarioReserva.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        idHorarioReserva.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Horario: " + e.getMessage());
        }
    }//fin de horarios

    private void cargarEstados() {
        File archivo = new File("ReservasEstado.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            IdEstadoReserva.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        IdEstadoReserva.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Estado: " + e.getMessage());
        }
    }//fin de Estados

}