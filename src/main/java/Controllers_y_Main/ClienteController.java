package Controllers_y_Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

public class ClienteController {
    @FXML private RadioButton Activo;
    @FXML private RadioButton Invitado;
    @FXML private TextField Notificador;
    @FXML private RadioButton Pasivo;
    @FXML private Button Salir;
    @FXML private RadioButton SocioActivo;
    @FXML private DatePicker fechaIngresoPicker;
    @FXML private DatePicker fechaNacPicker;
    @FXML private TextField txtApellidoMat;
    @FXML private TextField txtApellidoPat;
    @FXML private TextField txtBalance;
    @FXML private TextField txtCelular;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtIdCliente;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtValorCuota;
    @FXML private ToggleGroup GrupoTipo;
    @FXML private ToggleGroup GrupoStatus;
    @FXML private Button btnGuardar;
    @FXML private Button btnBorrar;
    @FXML private Button btnLimpiar;

    private static final String ARCHIVO_CLIENTES = "Clientes.txt";
    private boolean modificando = false;

    @FXML
    public void initialize()
    {
        txtIdCliente.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                validarIdCliente(newValue);
            } else {
                limpiarCampos(true);
                desactivarCampos();
                modificando = false;
                Notificador.clear();
            }
        });

        SocioActivo.setToggleGroup(GrupoTipo);
        Invitado.setToggleGroup(GrupoTipo);
        Activo.setToggleGroup(GrupoStatus);
        Pasivo.setToggleGroup(GrupoStatus);

        fechaIngresoPicker.setEditable(false);
    }

    private void validarIdCliente(String idCliente)
    {
        File archivo = new File(ARCHIVO_CLIENTES);

        if (!archivo.exists())
        {
            activarCampos();
            fechaIngresoPicker.setValue(LocalDate.now());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean encontrado = false;

            while ((linea = br.readLine()) != null && !linea.isEmpty()) {
                String[] partes = linea.split(":");
                if (partes.length >= 14 && partes[0].equals(idCliente)) {
                    cargarDatosCliente(partes);
                    encontrado = true;
                    modificando = true;
                    Notificador.setText("Modificando");
                    break;
                }
            }
            if (!encontrado)
            {
                Notificador.setText("Creando");
                limpiarCampos(false);
                activarCampos();
                fechaIngresoPicker.setValue(LocalDate.now());
                modificando = false;
            }
            else
            {
            }

        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de clientes: " + e.getMessage());
        }
    }

    private void cargarDatosCliente(String[] datos) {
        if (datos.length >= 14) {
            txtNombreCliente.setText(datos[1]);
            txtApellidoPat.setText(datos[2]);
            txtApellidoMat.setText(datos[3]);
            txtDireccion.setText(datos[4]);
            fechaNacPicker.setValue(LocalDate.parse(datos[5], DateTimeFormatter.ofPattern("d/M/yyyy")));
            txtTelefono.setText(datos[6]);
            txtCelular.setText(datos[7]);
            fechaIngresoPicker.setValue(LocalDate.parse(datos[8], DateTimeFormatter.ofPattern("d/M/yyyy")));
            txtCorreo.setText(datos[9]);
            txtBalance.setText(datos[10]);
            txtValorCuota.setText(datos[11]);


            if (datos[12].equals("Socio Activo")) {
                SocioActivo.setSelected(true);
            } else {
                Invitado.setSelected(true);
            }

            if (datos[13].equals("Activo")) {
                Activo.setSelected(true);
            } else {
                Pasivo.setSelected(true);
            }
        }
    }

    @FXML
    private void guardarCliente() {
        if (!validarCampos()) {
            return;
        }

        if (Invitado.isSelected() && Activo.isSelected()) {
            mostrarAlerta("Un invitado no puede tener status Activo");
            return;
        }

        String id = txtIdCliente.getText().trim();
        String nombre = txtNombreCliente.getText().trim();
        String apellidoPat = txtApellidoPat.getText().trim();
        String apellidoMat = txtApellidoMat.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String fechaNac = fechaNacPicker.getValue().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        String telefono = txtTelefono.getText().trim();
        String celular = txtCelular.getText().trim();
        String fechaIngreso = fechaIngresoPicker.getValue().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        String correo = txtCorreo.getText().trim();
        String balance = txtBalance.getText().trim();
        String valorCuota = txtValorCuota.getText().trim();
        String tipoCliente = SocioActivo.isSelected() ? "Socio Activo" : "Invitado";
        String statusCliente = Activo.isSelected() ? "Activo" : "Pasivo";

        String linea = String.join(":",
                id, nombre, apellidoPat, apellidoMat, direccion, fechaNac,
                telefono, celular, fechaIngreso, correo, balance, valorCuota,
                tipoCliente, statusCliente
        );

        File archivo = new File(ARCHIVO_CLIENTES);
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

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo)))
            {
                for (String l : lineas)
                {
                    bw.write(l);
                    bw.newLine();
                }
            }
            JOptionPane.showMessageDialog(null, "Cliente guardado exitosamente");

        } catch (IOException e) {
            mostrarAlerta("Error al guardar el cliente: " + e.getMessage());
        }
    }

    @FXML
    private void borrarCliente() {
        String idCliente = txtIdCliente.getText().trim();

        if (idCliente.isEmpty()) {
            mostrarAlerta("Ingrese un ID de cliente para borrar");
            return;
        }

        try {
            double balance = Double.parseDouble(txtBalance.getText().trim());
            if (balance > 0) {
                mostrarAlerta("No se puede borrar un cliente con balance mayor a cero");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error al validar el balance del cliente");
            return;
        }

        File archivo = new File(ARCHIVO_CLIENTES);
        File papelera = new File("papelera.txt");
        List<String> lineas = new ArrayList<>();
        String clienteAEliminar = null;
        boolean encontrado = false;

        try {
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        if (!linea.trim().isEmpty()) {
                            String[] partes = linea.split(":");
                            if (partes.length > 0 && partes[0].equals(idCliente)) {
                                clienteAEliminar = linea;
                                encontrado = true;
                            } else {
                                lineas.add(linea);
                            }
                        }
                    }
                }

                if (encontrado) {
                    try (BufferedWriter bwPapelera = new BufferedWriter(new FileWriter(papelera, true))) {
                        bwPapelera.write(clienteAEliminar);
                        bwPapelera.newLine();
                    }

                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                        for (String l : lineas) {
                            bw.write(l);
                            bw.newLine();
                        }
                    }

                    limpiarCampos(true);
                    mostrarAlerta("Cliente eliminado exitosamente");
                } else {
                    mostrarAlerta("No se encontró el cliente con ID: " + idCliente);
                }
            } else {
                mostrarAlerta("No existe el archivo de clientes");
            }
        } catch (IOException e) {
            mostrarAlerta("Error al borrar el cliente: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarCampos()
    {
        limpiarCampos(true);
    }

    private void limpiarCampos(boolean incluirId) {
        if (incluirId)
        {
            txtIdCliente.clear();
        }
        txtNombreCliente.clear();
        txtApellidoPat.clear();
        txtApellidoMat.clear();
        txtDireccion.clear();
        fechaNacPicker.setValue(null);
        txtTelefono.clear();
        txtCelular.clear();
        fechaIngresoPicker.setValue(null);
        txtCorreo.clear();
        txtBalance.clear();
        txtValorCuota.clear();
        GrupoTipo.selectToggle(null);
        GrupoStatus.selectToggle(null);
        desactivarCampos();
        modificando = false;
    }

    @FXML
    private void salir()
    {
        Stage stageActual = (Stage) Notificador.getScene().getWindow();
        stageActual.close();
    }

    private boolean validarCampos() {
        if (txtIdCliente.getText().trim().isEmpty() ||
                txtNombreCliente.getText().trim().isEmpty() ||
                txtApellidoPat.getText().trim().isEmpty() ||
                txtDireccion.getText().trim().isEmpty() ||
                fechaNacPicker.getValue() == null ||
                txtTelefono.getText().trim().isEmpty() ||
                txtCelular.getText().trim().isEmpty() ||
                txtCorreo.getText().trim().isEmpty() ||
                txtBalance.getText().trim().isEmpty() ||
                txtValorCuota.getText().trim().isEmpty() ||
                GrupoTipo.getSelectedToggle() == null ||
                GrupoStatus.getSelectedToggle() == null) {

            mostrarAlerta("Todos los campos son obligatorios");
            return false;
        }

        try {
        int idCliente = Integer.parseInt(txtIdCliente.getText());
        }catch (NumberFormatException e)
        {
            mostrarAlerta("El  id del cliente tiene que ser un entero");
            return false;
        }

        try {
            int telefono = Integer.parseInt(txtTelefono.getText());
        }catch (NumberFormatException e)
        {
            mostrarAlerta("El telefono tiene que ser solo numeros enteros");
            return false;
        }

        try {
            int Celular = Integer.parseInt(txtCelular.getText());
        }catch (NumberFormatException e)
        {
            mostrarAlerta("Error el Celular tiene que ser solo numeros enteros");
            return false;
        }

        if (!txtCorreo.getText().trim().contains("@")) {
            mostrarAlerta("Ingrese un correo electrónico válido");
            return false;
        }

        try {
            Double.parseDouble(txtBalance.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("El balance debe ser un valor numérico");
            return false;
        }

        try {
            Double.parseDouble(txtValorCuota.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("El valor de cuota debe ser un valor numérico");
            return false;
        }

        return true;
    }

    private void activarCampos() {
        txtNombreCliente.setDisable(false);
        txtApellidoPat.setDisable(false);
        txtApellidoMat.setDisable(false);
        txtDireccion.setDisable(false);
        fechaNacPicker.setDisable(false);
        txtTelefono.setDisable(false);
        txtCelular.setDisable(false);
        txtCorreo.setDisable(false);
        txtValorCuota.setDisable(false);
        fechaIngresoPicker.setDisable(false);
        SocioActivo.setDisable(false);
        Invitado.setDisable(false);
        Activo.setDisable(false);
        Pasivo.setDisable(false);
    }

    private void desactivarCampos() {
        txtNombreCliente.setDisable(true);
        txtApellidoPat.setDisable(true);
        txtApellidoMat.setDisable(true);
        txtDireccion.setDisable(true);
        fechaNacPicker.setDisable(true);
        fechaIngresoPicker.setDisable(true);
        txtTelefono.setDisable(true);
        txtCelular.setDisable(true);
        txtCorreo.setDisable(true);
        txtValorCuota.setDisable(true);
        SocioActivo.setDisable(true);
        Invitado.setDisable(true);
        Activo.setDisable(true);
        Pasivo.setDisable(true);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}