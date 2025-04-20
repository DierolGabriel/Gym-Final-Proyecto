package Controllers_y_Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GenerarCobroControll implements Initializable {

    // Columnas de la tabla
    @FXML private TableColumn<Usuario, String> col_id_Usuario;
    @FXML private TableColumn<Usuario, String> col_Nombre;
    @FXML private TableColumn<Usuario, String> col_Apellido_Paterno;
    @FXML private TableColumn<Usuario, String> col_Apellido_Materno;
    @FXML private TableColumn<Usuario, String> col_Direccion;
    @FXML private TableColumn<Usuario, String> col_FechaNacimiento;
    @FXML private TableColumn<Usuario, String> col_Telefono;
    @FXML private TableColumn<Usuario, String> col_FechaInicio;
    @FXML private TableColumn<Usuario, String> col_Email;
    @FXML private TableColumn<Usuario, String> col_Valor_de_Cuota;
    @FXML private TableColumn<Usuario, String> col_Balance;
    @FXML private TableColumn<Usuario, String> col_Status;
    @FXML private TableColumn<Usuario, String> col_Tipo;

    @FXML private TableView<Usuario> Usuario;
    @FXML private DatePicker Calendario;
    @FXML private Button BtSalir;
    @FXML private Button Btprocesar;
    @FXML private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarUsuarios();  // Cargar los usuarios desde el archivo
        configurarDatePicker();

        // Configurar la acción del botón "Procesar"
        Btprocesar.setOnAction(event -> generarCobros());
    }

    private void configurarColumnas() {
        col_id_Usuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        col_Nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_Apellido_Paterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        col_Apellido_Materno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        col_Direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_FechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        col_Telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_FechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        col_Email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_Valor_de_Cuota.setCellValueFactory(new PropertyValueFactory<>("valorCuota"));
        col_Balance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        col_Status.setCellValueFactory(new PropertyValueFactory<>("status"));
        col_Tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
    }

    private void cargarUsuarios() {
        Usuario.getItems().clear();
        try (BufferedReader br = new BufferedReader(new FileReader("Clientes.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");

                if (partes.length >= 14) {
                    Usuario usuario = new Usuario(
                            partes[0],    // idUsuario
                            partes[1],    // nombre
                            partes[2],    // apellidoPaterno
                            partes[3],    // apellidoMaterno
                            partes[4],    // direccion
                            partes[5],    // fechaNacimiento
                            partes[6],    // telefono
                            partes[7],    // fechaInicio
                            partes[8],    // email
                            partes[9],    // valorCuota
                            partes[10],   // balance
                            partes[11],   // status
                            partes[12]    // tipo
                    );
                    Usuario.getItems().add(usuario);
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al cargar clientes",
                    "Verifique que el archivo Clientes.txt exista y tenga el formato correcto");
            e.printStackTrace();
        }
    }

    // Método para configurar el DatePicker
    private void configurarDatePicker() {
        Calendario.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                return (object != null) ? object.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string) : null;
            }
        });
    }

    // Método para generar los cobros
    private void generarCobros() {
        // Ejemplo de acción cuando se presiona el botón "Procesar"
        lblMensaje.setText("Cobros procesados correctamente.");

        // Aquí puedes agregar la lógica para generar cobros, calcular montos, o lo que sea necesario
        // Ejemplo de recorrer los usuarios y procesar la cuota
        for (Usuario usuario : Usuario.getItems()) {
            // Aquí puedes acceder a las propiedades de cada usuario, por ejemplo:
            String id = usuario.getIdUsuario();
            String nombre = usuario.getNombre();
            String cuota = usuario.getValorCuota();

            // Realizar las acciones correspondientes, como generar cobros o cálculos adicionales
            System.out.println("Procesando cobro para: " + nombre + " (ID: " + id + ") con cuota: " + cuota);
        }
    }

    // Método para mostrar alertas
    private void mostrarAlerta(String titulo, String cabecera, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public static class Usuario {
        private final String idUsuario;
        private final String nombre;
        private final String apellidoPaterno;
        private final String apellidoMaterno;
        private final String direccion;
        private final String fechaNacimiento;
        private final String telefono;
        private final String fechaInicio;
        private final String email;
        private final String valorCuota;
        private final String balance;
        private final String status;
        private final String tipo;

        public Usuario(String idUsuario, String nombre, String apellidoPaterno,
                       String apellidoMaterno, String direccion, String fechaNacimiento,
                       String telefono, String fechaInicio, String email, String valorCuota,
                       String balance, String status, String tipo) {
            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.apellidoPaterno = apellidoPaterno;
            this.apellidoMaterno = apellidoMaterno;
            this.direccion = direccion;
            this.fechaNacimiento = fechaNacimiento;
            this.telefono = telefono;
            this.fechaInicio = fechaInicio;
            this.email = email;
            this.valorCuota = valorCuota;
            this.balance = balance;
            this.status = status;
            this.tipo = tipo;
        }

        // Getters para todas las propiedades
        public String getIdUsuario() { return idUsuario; }
        public String getNombre() { return nombre; }
        public String getApellidoPaterno() { return apellidoPaterno; }
        public String getApellidoMaterno() { return apellidoMaterno; }
        public String getDireccion() { return direccion; }
        public String getFechaNacimiento() { return fechaNacimiento; }
        public String getTelefono() { return telefono; }
        public String getFechaInicio() { return fechaInicio; }
        public String getEmail() { return email; }
        public String getValorCuota() { return valorCuota; }
        public String getBalance() { return balance; }
        public String getStatus() { return status; }
        public String getTipo() { return tipo; }
    }
}
