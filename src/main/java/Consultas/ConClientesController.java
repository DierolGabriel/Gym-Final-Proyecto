package Consultas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConClientesController {

    @FXML private Button Consultar;
    @FXML private RadioButton Filtro1;
    @FXML private RadioButton Filtro2;
    @FXML private RadioButton Filtro3;
    @FXML private RadioButton Filtro4;
    @FXML private ToggleGroup Grupo1;
    @FXML private Button Limpiar;
    @FXML private TableView<Cliente> Table;
    @FXML private TextField TextField;

    // Columnas de la tabla
    @FXML private TableColumn<Cliente, String> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellido;
    @FXML private TableColumn<Cliente, String> colApellido1;
    @FXML private TableColumn<Cliente, String> colTelefono1;
    @FXML private TableColumn<Cliente, String> colCorreo1;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colApellido2;
    @FXML private TableColumn<Cliente, String> colCorreo11;
    @FXML private TableColumn<Cliente, String> colCorreo111;
    @FXML private TableColumn<Cliente, String> colCorreo1111;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colCorreo2;
    @FXML private TableColumn<Cliente, String> colCorreo21;
    @FXML private CheckBox Filtro5;

    private static final String ARCHIVO_CLIENTES = "Clientes.txt";
    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        configurarColumnas();

        // Cargar los datos del archivo
        cargarClientes();
        configurarListenerID();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApellido1.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colTelefono1.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCorreo1.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colApellido2.setCellValueFactory(new PropertyValueFactory<>("celular"));
        colCorreo11.setCellValueFactory(new PropertyValueFactory<>("fechaIngreso"));
        colCorreo111.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCorreo1111.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCorreo2.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colCorreo21.setCellValueFactory(new PropertyValueFactory<>("valorCuota"));
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

    private void cargarClientes() {
        listaClientes.clear();
        File archivo = new File(ARCHIVO_CLIENTES);

        if (!archivo.exists()) {
            mostrarAlerta("El archivo de clientes no existe");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 14) {
                        Cliente cliente = new Cliente(
                                partes[0].trim(),  // id
                                partes[1].trim(),  // nombre
                                partes[2].trim(),  // apellidoPaterno
                                partes[3].trim(),  // apellidoMaterno
                                partes[4].trim(),  // direccion
                                partes[5].trim(),  // fechaNacimiento
                                partes[6].trim(),  // telefono
                                partes[7].trim(),  // celular
                                partes[8].trim(),  // fechaIngreso
                                partes[9].trim(),  // correo
                                partes[10].trim(), // balance
                                partes[11].trim(), // valorCuota
                                partes[12].trim(), // status
                                partes[13].trim()  // tipo
                        );
                        listaClientes.add(cliente);
                    }
                }
            }
            Table.setItems(listaClientes);
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de clientes: " + e.getMessage());
        }
    }

    @FXML
    void Consultar(ActionEvent event) {
        String filtro = TextField.getText().trim().toLowerCase();
        boolean soloBalancePendiente = Filtro5.isSelected();

        if (filtro.isEmpty() && !soloBalancePendiente) {
            Table.setItems(listaClientes);
            return;
        }

        ObservableList<Cliente> clientesFiltrados = FXCollections.observableArrayList();

        for (Cliente cliente : listaClientes) {
            boolean coincideConFiltro = false;

            // Aplicar filtros de radio buttons
            if (filtro.isEmpty()) {
                coincideConFiltro = true; // Si no hay texto, mostrar todos (si el checkbox está activo)
            } else if (Filtro1.isSelected() && cliente.getId().toLowerCase().contains(filtro)) {
                coincideConFiltro = true;
            } else if (Filtro2.isSelected() && cliente.getNombre().toLowerCase().contains(filtro)) {
                coincideConFiltro = true;
            } else if (Filtro3.isSelected() && cliente.getTipo().toLowerCase().contains(filtro)) {
                coincideConFiltro = true;
            } else if (Filtro4.isSelected() && cliente.getStatus().toLowerCase().contains(filtro)) {
                coincideConFiltro = true;
            }

            // Aplicar filtro de balance pendiente (si está activo)
            if (soloBalancePendiente) {
                try {
                    double balance = Double.parseDouble(cliente.getBalance());
                    if (balance <= 0) {
                        continue; // Saltar clientes con balance <= 0
                    }
                } catch (NumberFormatException e) {
                    continue; // Saltar clientes con balance no numérico
                }
            }

            // Si coincide con los filtros y pasa el filtro de balance (si aplica)
            if ((filtro.isEmpty() || coincideConFiltro) && (!soloBalancePendiente || (soloBalancePendiente && tieneBalancePendiente(cliente))))
            {
                clientesFiltrados.add(cliente);
            }
        }

        Table.setItems(clientesFiltrados);
    }

    private boolean tieneBalancePendiente(Cliente cliente) {
        try {
            double balance = Double.parseDouble(cliente.getBalance());
            return balance > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    @FXML
    void Limpiar(ActionEvent event) {
        TextField.clear();
        Table.setItems(listaClientes);
        Filtro1.setSelected(true); // Seleccionar el primer filtro por defecto
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class Cliente {
        private String id;
        private String nombre;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private String direccion;
        private String fechaNacimiento;
        private String telefono;
        private String celular;
        private String fechaIngreso;
        private String correo;
        private String balance;
        private String valorCuota;
        private String status;
        private String tipo;

        public Cliente(String id, String nombre, String apellidoPaterno, String apellidoMaterno,
                       String direccion, String fechaNacimiento, String telefono, String celular,
                       String fechaIngreso, String correo, String balance, String valorCuota,
                       String status, String tipo) {
            this.id = id;
            this.nombre = nombre;
            this.apellidoPaterno = apellidoPaterno;
            this.apellidoMaterno = apellidoMaterno;
            this.direccion = direccion;
            this.fechaNacimiento = fechaNacimiento;
            this.telefono = telefono;
            this.celular = celular;
            this.fechaIngreso = fechaIngreso;
            this.correo = correo;
            this.balance = balance;
            this.valorCuota = valorCuota;
            this.status = status;
            this.tipo = tipo;
        }

        // Getters
        public String getId() { return id; }
        public String getNombre() { return nombre; }
        public String getApellidoPaterno() { return apellidoPaterno; }
        public String getApellidoMaterno() { return apellidoMaterno; }
        public String getDireccion() { return direccion; }
        public String getFechaNacimiento() { return fechaNacimiento; }
        public String getTelefono() { return telefono; }
        public String getCelular() { return celular; }
        public String getFechaIngreso() { return fechaIngreso; }
        public String getCorreo() { return correo; }
        public String getBalance() { return balance; }
        public String getValorCuota() { return valorCuota; }
        public String getStatus() { return status; }
        public String getTipo() { return tipo; }
    }
}
