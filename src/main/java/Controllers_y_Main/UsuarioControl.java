package Controllers_y_Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioControl {

    @FXML
    private RadioButton Administrador;

    @FXML
    private ToggleGroup Grupo1;

    @FXML
    private Button Guardar;

    @FXML
    private Button Limpiar;

    @FXML
    private Button Salir;

    @FXML
    private RadioButton User;

    @FXML
    private Pane Usuario;

    @FXML
    private TextField apellidoField;

    @FXML
    private TextField correoField;

    @FXML
    private TextField loginField;

    @FXML
    private TextField nombreField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField statusLabel;

    @FXML
    void Detectar(ActionEvent event) {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty())
        {
            mostrarAlerta("Ingrese el nombre del usuario y la contraseña");
            Stage stageActual = (Stage) User.getScene().getWindow();
            stageActual.toFront();
            return;
        }
        String loginFieldText = loginField.getText();
        String password = passwordField.getText();

        File arq = new File("archivo.txt");
        boolean encontrado = false;

        try {
            if (!arq.exists()) {
                arq.createNewFile();
            }

            try (BufferedReader br = new BufferedReader(new FileReader(arq))) {
                String linea;

                while ((linea = br.readLine()) != null && !linea.isEmpty()) {
                    String[] partes = linea.split(":");

                    if (partes.length >= 6)
                    {
                        try {
                            String Login = partes[0];
                            String passwordusuario = partes[1];

                            if (Login.equals(loginFieldText) && password.equals(passwordusuario))
                            {
                                int Nivel = Integer.parseInt(partes[2]);
                                String Nombre = partes[3];
                                String Apellido = partes[4];
                                String Correo = partes[5];

                                if (Nivel == 1) {
                                    Administrador.setSelected(true);
                                    User.setSelected(false);
                                } else {
                                    User.setSelected(true);
                                    Administrador.setSelected(false);
                                }

                                nombreField.setText(Nombre.trim());
                                apellidoField.setText(Apellido.trim());
                                correoField.setText(Correo.trim());
                                statusLabel.setText("Modificado");
                                encontrado = true;
                                activar();
                                loginField.setDisable(true);
                                break;
                            }
                            if(passwordusuario != passwordField.getText() && Login.equals(loginFieldText))
                            {
                                mostrarAlerta("Este usuario ya existe, ingrese la contraseña correcta o digite otro usuario.");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                }
            }
            if (!encontrado)
            {
                activar();
                statusLabel.setText("Creando");
                nombreField.setText("");
                apellidoField.setText("");
                correoField.setText("");
                User.setSelected(false);
                Administrador.setSelected(false);
            }
        } catch (IOException e) {
            mostrarAlerta("Error al crear el usuario");
        }
    }

    @FXML
    void Guardar(ActionEvent event)
    {
        if (!validarCamposCompletos())
        {
            mostrarAlerta("Todos los campos deden estar llenos");
            Stage stageActual = (Stage) User.getScene().getWindow();
            return;
        }

        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        int nivel = Administrador.isSelected() ? 1 : 0 ;
        String nombre = nombreField.getText().trim();
        String apellido = apellidoField.getText().trim();
        String correo = correoField.getText().trim();

        File archivo = new File("archivo.txt");
        List<String> lineas = new ArrayList<>();
        boolean usuarioExiste = false;

        try {
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        if (!linea.trim().isEmpty()) {
                            String[] partes = linea.split(":");
                            if (partes.length >= 1 && partes[0].equals(login)) {
                                lineas.add(crearLineaUsuario(login, password, nivel, nombre, apellido, correo));
                                usuarioExiste = true;
                            } else {
                                lineas.add(linea);
                            }
                        }
                    }
                }
            }

            if (!usuarioExiste) {
                lineas.add(crearLineaUsuario(login, password, nivel, nombre, apellido, correo));
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (String linea : lineas) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            mostrarAlerta("Usuario guardado correctamente");
            statusLabel.setText(usuarioExiste ? "Modificado" : "Creado");
            desactivar();
        } catch (IOException e)
        {
            mostrarAlerta("Error al guardar " + e.getMessage());
        }
    }

    private String crearLineaUsuario(String login, String password, int nivel, String nombre, String apellido, String correo) {
        return String.join(":",
                login,
                password,
                String.valueOf(nivel),
                nombre,
                apellido,
                correo
        );
    }

    private boolean validarCamposCompletos() {
        return !loginField.getText().trim().isEmpty() &&
                !passwordField.getText().trim().isEmpty() &&
                !nombreField.getText().trim().isEmpty() &&
                !apellidoField.getText().trim().isEmpty() &&
                !correoField.getText().trim().isEmpty() &&
                (User.isSelected() || Administrador.isSelected());
    }
    @FXML
    void Limpiar(ActionEvent event)
    {
        desactivar();
        nombreField.setText("");
        loginField.setText("");
        apellidoField.setText("");
        correoField.setText("");
        passwordField.setText("");
    }

    @FXML
    void Salir(ActionEvent event)
    {
        Stage stageActual = (Stage) User.getScene().getWindow();
        stageActual.close();
    }

    public void activar()
    {
        loginField.setDisable(false);
        Administrador.setDisable(false);
        User.setDisable(false);
        nombreField.setDisable(false);
        apellidoField.setDisable(false);
        correoField.setDisable(false);
    }

    public void desactivar()
    {
        loginField.setDisable(false);
        Administrador.setDisable(true);
        User.setDisable(true);
        Administrador.setSelected(false);
        User.setSelected(false);
        nombreField.setDisable(true);
        apellidoField.setDisable(true);
        correoField.setDisable(true);
        statusLabel.setText("");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
