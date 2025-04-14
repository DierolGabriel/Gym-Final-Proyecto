package Consultas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConUsuariosController {

    @FXML private RadioButton Filtro1;
    @FXML private RadioButton Filtro2;
    @FXML private Button Limpiar;
    @FXML private TableView<Usuario> Table;
    @FXML private TextField TextField;
    @FXML private ToggleGroup grupo1;

    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colNivelAcceso;
    @FXML private TableColumn<Usuario, String> colApellido;
    @FXML private TableColumn<Usuario, String> colEmail;

    private static final String ARCHIVO_USUARIOS = "archivo.txt";
    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNivelAcceso.setCellValueFactory(new PropertyValueFactory<>("nivelAcceso"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        cargarUsuarios();
        configurarListenerID();
    }
    private void configurarListenerID() {
        TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty())
            {
                Consultar(null);
            }
            else
            {
                Consultar(null);
            }
        });
    }

    private void cargarUsuarios() {
        listaUsuarios.clear();
        File archivo = new File(ARCHIVO_USUARIOS);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de usuarios no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 6) {
                        Usuario usuario = new Usuario(
                                partes[0], // usuario
                                partes[3], // nombre
                                partes[2], // nivel acceso
                                partes[4], // apellido
                                partes[5]  // email
                        );
                        listaUsuarios.add(usuario);
                    }
                }
            }
            Table.setItems(listaUsuarios);
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de usuarios: " + e.getMessage());
        }
    }

    @FXML
    void Consultar(ActionEvent event) {
        String filtro = TextField.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            Table.setItems(listaUsuarios);
            return;
        }

        ObservableList<Usuario> usuariosFiltrados = FXCollections.observableArrayList();

        for (Usuario usuario : listaUsuarios) {
            if (Filtro1.isSelected()) {
                if (usuario.getUsuario().toLowerCase().contains(filtro)) {
                    usuariosFiltrados.add(usuario);
                }
            } else if (Filtro2.isSelected()) {
                if (usuario.getNombre().toLowerCase().contains(filtro)) {
                    usuariosFiltrados.add(usuario);
                }
            }
        }

        Table.setItems(usuariosFiltrados);
    }

    @FXML
    void Limpiar(ActionEvent event) {
        Stage stageActual = (Stage) TextField.getScene().getWindow();
        stageActual.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class Usuario {
        private String usuario;
        private String nombre;
        private String nivelAcceso;
        private String apellido;
        private String email;

        public Usuario(String usuario, String nombre, String nivelAcceso, String apellido, String email) {
            this.usuario = usuario;
            this.nombre = nombre;
            this.nivelAcceso = nivelAcceso;
            this.apellido = apellido;
            this.email = email;
        }

        public String getUsuario() { return usuario; }
        public String getNombre() { return nombre; }
        public String getNivelAcceso() { return nivelAcceso; }
        public String getApellido() { return apellido; }
        public String getEmail() { return email; }
    }
}