package Controllers_y_Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.*;

public class ActividadesController {

    @FXML private TextField txtIdAct;
    @FXML private TextField txtNombreAct;
    @FXML private TextField txtDescripcionAct;
    @FXML private ComboBox<String> txtIdEntrenadorAct;
    @FXML private ComboBox<String> txtIdLocalizacionAct;
    @FXML private Label lblEstado;
    @FXML private Button Limpiar;
    @FXML private Button Salir;

    private final String archivoActividades ="Actividades.txt";
    private final String archivoLocalizaciones ="Localización.txt";
    private final String archivoEntrenadores = "Entrenadores.txt";

    @FXML
    public void initialize() {
        crearArchivoSiNoExiste(archivoActividades);
        crearArchivoSiNoExiste(archivoLocalizaciones);
        crearArchivoSiNoExiste(archivoEntrenadores);
        txtIdAct.setOnKeyReleased(event -> verificarActividad());
        cargarActvidades();
        Desactivar();
    }

    private void cargarActvidades() {
        File archivo = new File("Entrenadores.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            txtIdEntrenadorAct.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1) {
                        txtIdEntrenadorAct.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Entrenadores: " + e.getMessage());
        }

        File archivo2 = new File("Localización.txt");
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo2))) {
            txtIdLocalizacionAct.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 1)
                    {
                        txtIdLocalizacionAct.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar localizaciones: " + e.getMessage());
        }
    }

    private void crearArchivoSiNoExiste(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo)))
            {
            } catch (IOException e) {
                mostrarAlerta("Error al crear " + rutaArchivo);
            }
        }
    }

    private void verificarActividad() {
        String id = txtIdAct.getText().trim();
        if (id.isEmpty()) return;

        File archivo = new File(archivoActividades);
        if (!archivo.exists()) return;

        boolean encontrado = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(":");
                if (datos.length >= 5 && datos[0].equals(id)) {
                    txtNombreAct.setText(datos[1]);
                    txtDescripcionAct.setText(datos[2]);
                    txtIdLocalizacionAct.setValue(datos[3]);
                    txtIdEntrenadorAct.setValue(datos[4]);
                    lblEstado.setText("Estado: Modificando");
                    Activar();
                    lblEstado.setStyle("-fx-text-fill: orange;");
                    encontrado = true;
                    break;
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al leer Actividades.txt");
        }

        if (!encontrado) {
            limpiarCampos(false);
            lblEstado.setText("Estado: Creando");
            Activar();
            lblEstado.setStyle("-fx-text-fill: green;");
        }
    }

    private void limpiarCampos(boolean incluirId) {
        if (incluirId) txtIdAct.clear();
        txtNombreAct.clear();
        txtDescripcionAct.clear();
        txtIdLocalizacionAct.setValue(null);
        txtIdEntrenadorAct.setValue(null);
    }

    private void agregarNuevoRegistro(String archivoRuta, String id, String descripcion) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoRuta, true))) {
            writer.write(id + ":" + descripcion + "\n");
            System.out.println("Se agregó " + id + " en " + archivoRuta);
        } catch (IOException e) {
            mostrarAlerta("Error al escribir en " + archivoRuta);
        }
    }

    @FXML
    private void guardarActividad()
    {
        if (camposVacios())
        {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }

        String idLocalizacion = txtIdLocalizacionAct.getValue().trim();
        String idEntrenador = txtIdEntrenadorAct.getValue().trim();

        String id = txtIdAct.getText().trim();
        String lineaNueva = String.join(":",
                id,
                txtNombreAct.getText().trim(),
                txtDescripcionAct.getText().trim(),
                idLocalizacion,
                idEntrenador
        );

        File archivo = new File(archivoActividades);
        boolean actualizado = false;
        StringBuilder nuevoContenido = new StringBuilder();

        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    String[] datos = linea.split(":");
                    if (datos[0].equals(id)) {
                        nuevoContenido.append(lineaNueva).append("\n");
                        actualizado = true;
                    } else {
                        nuevoContenido.append(linea).append("\n");
                    }
                }
            } catch (IOException e) {
                mostrarAlerta("Error al actualizar archivo.");
                return;
            }
        }

        if (!actualizado) {
            nuevoContenido.append(lineaNueva).append("\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write(nuevoContenido.toString());
            mostrarAlerta("Actividad guardada correctamente.");
            lblEstado.setStyle("-fx-text-fill: blue;");
            limpiarCampos(true);
            Desactivar();

        } catch (IOException e) {
            mostrarAlerta("Error al guardar en Actividades.txt");
        }
    }

    private boolean camposVacios() {
        return txtIdAct.getText().isEmpty()
                || txtNombreAct.getText().isEmpty()
                || txtDescripcionAct.getText().isEmpty()
                || txtIdLocalizacionAct.getValue().isEmpty()
                || txtIdEntrenadorAct.getValue().isEmpty();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void Salir(javafx.event.ActionEvent actionEvent)
    {
        Stage stageActual = (Stage) txtDescripcionAct.getScene().getWindow();
        stageActual.close();
    }

    @FXML
    void Limpiar(javafx.event.ActionEvent actionEvent)
    {
    txtIdAct.setText("");
    txtNombreAct.setText("");
    txtDescripcionAct.setText("");
    txtIdLocalizacionAct.setValue(null);
    txtIdEntrenadorAct.setValue(null);
    txtNombreAct.setDisable(true);
    txtDescripcionAct.setDisable(true);
    txtIdLocalizacionAct.setDisable(true);
    txtIdEntrenadorAct.setDisable(true);
    txtDescripcionAct.setDisable(true);
    lblEstado.setText("Estado:");
    }

    void Activar()
    {
        txtNombreAct.setDisable(false);
        txtDescripcionAct.setDisable(false);
        txtIdLocalizacionAct.setDisable(false);
        txtIdEntrenadorAct.setDisable(false);
        txtDescripcionAct.setDisable(false);
    }

    void Desactivar()
    {
        txtNombreAct.setDisable(true);
        txtDescripcionAct.setDisable(true);
        txtIdLocalizacionAct.setDisable(true);
        txtIdEntrenadorAct.setDisable(true);
        txtDescripcionAct.setDisable(true);
    }


}
