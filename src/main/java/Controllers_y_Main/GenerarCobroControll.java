package Controllers_y_Main;

import Consultas.ConClientesController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class GenerarCobroControll {

    @FXML private Button BtSalir;
    @FXML private Button Btprocesar;
    @FXML private DatePicker Calendario;
    @FXML private TableView<ConClientesController.Cliente> Table;
    @FXML private Label lblMensaje;

    // Table columns declarations
    @FXML private TableColumn<ConClientesController.Cliente, String> colId;
    @FXML private TableColumn<ConClientesController.Cliente, String> colNombre;
    @FXML private TableColumn<ConClientesController.Cliente, String> colApellido;
    @FXML private TableColumn<ConClientesController.Cliente, String> colApellido1;
    @FXML private TableColumn<ConClientesController.Cliente, String> colTelefono1;
    @FXML private TableColumn<ConClientesController.Cliente, String> colCorreo1;
    @FXML private TableColumn<ConClientesController.Cliente, String> colTelefono;
    @FXML private TableColumn<ConClientesController.Cliente, String> colApellido2;
    @FXML private TableColumn<ConClientesController.Cliente, String> colCorreo11;
    @FXML private TableColumn<ConClientesController.Cliente, String> colCorreo111;
    @FXML private TableColumn<ConClientesController.Cliente, String> colCorreo1111;
    @FXML private TableColumn<ConClientesController.Cliente, String> colCorreo;
    @FXML private TableColumn<ConClientesController.Cliente, String> colCorreo2;
    @FXML private TableColumn<ConClientesController.Cliente, String> colCorreo21;

    private static final String ARCHIVO_CLIENTES = "Clientes.txt";
    private static final String ARCHIVO_COBROS = "Cobro.txt";
    private ObservableList<ConClientesController.Cliente> listaClientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarClientes();
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
                        ConClientesController.Cliente cliente = new ConClientesController.Cliente(
                                partes[0].trim(),
                                partes[1].trim(),
                                partes[2].trim(),
                                partes[3].trim(),
                                partes[4].trim(),
                                partes[5].trim(),
                                partes[6].trim(),
                                partes[7].trim(),
                                partes[8].trim(),
                                partes[9].trim(),
                                partes[10].trim(),
                                partes[11].trim(),
                                partes[13].trim(),
                                partes[12].trim()
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
    private void Procesar() throws IOException {
        if (Calendario.getValue() == null) {
            mostrarAlerta("Seleccione una fecha para procesar el cobro");
            return;
        }

        LocalDate fechaSeleccionada = Calendario.getValue();
        String mes = obtenerNombreMes(fechaSeleccionada.getMonthValue());
        String fechaFormateada = fechaSeleccionada.format(DateTimeFormatter.ofPattern("d/M/yyyy"));

        // Verificar si ya existe un cobro para este mes
        if (existeCobroParaMes(mes)) {
            mostrarAlerta("Ya se ha generado un cobro para el mes de " + mes);
            return;
        }

        int ultimoIdCobro = obtenerUltimoIdCobro();
        int nuevoIdCobro = ultimoIdCobro + 1;

        List<String> cobrosEntries = new ArrayList<>();
        List<ConClientesController.Cliente> clientesActualizados = new ArrayList<>();

        for (int i = 0; i < listaClientes.size(); i++) {
            ConClientesController.Cliente cliente = listaClientes.get(i);
            if ("Activo".equals(cliente.getStatus())) {
                try {
                    double balanceActual = Double.parseDouble(cliente.getBalance());
                    double valorCuota = Double.parseDouble(cliente.getValorCuota());
                    double nuevoBalance = balanceActual + valorCuota;

                    // Actualizar el balance del cliente
                    ConClientesController.Cliente clienteActualizado = new ConClientesController.Cliente(
                            cliente.getId(),
                            cliente.getNombre(),
                            cliente.getApellidoPaterno(),
                            cliente.getApellidoMaterno(),
                            cliente.getDireccion(),
                            cliente.getFechaNacimiento(),
                            cliente.getTelefono(),
                            cliente.getCelular(),
                            cliente.getFechaIngreso(),
                            cliente.getCorreo(),
                            String.valueOf(nuevoBalance),
                            cliente.getValorCuota(),
                            cliente.getStatus(),
                            cliente.getTipo()
                    );
                    listaClientes.set(i, clienteActualizado);
                    clientesActualizados.add(clienteActualizado);

                    // Crear entrada de cobro
                    String cobroLine = String.format("%d:%s:%s:%s:cobro de %s:false",
                            nuevoIdCobro,
                            fechaFormateada,
                            cliente.getId(),
                            cliente.getValorCuota(),
                            mes);
                    cobrosEntries.add(cobroLine);
                    nuevoIdCobro++;
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error en el balance o valor de cuota del cliente " + cliente.getId());
                    return;
                }
            }
        }

        if (cobrosEntries.isEmpty()) {
            mostrarAlerta("No hay clientes activos para procesar.");
            return;
        }

        // Guardar cobros en Cobro.txt
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_COBROS, true))) {
            for (String entry : cobrosEntries) {
                bw.write(entry);
                bw.newLine();
            }
        } catch (IOException e) {
            mostrarAlerta("Error al guardar los cobros: " + e.getMessage());
            return;
        }

        // Actualizar Clientes.txt
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_CLIENTES))) {
            for (ConClientesController.Cliente cliente : listaClientes) {
                String linea = String.join(":",
                        cliente.getId(),
                        cliente.getNombre(),
                        cliente.getApellidoPaterno(),
                        cliente.getApellidoMaterno(),
                        cliente.getDireccion(),
                        cliente.getFechaNacimiento(),
                        cliente.getTelefono(),
                        cliente.getCelular(),
                        cliente.getFechaIngreso(),
                        cliente.getCorreo(),
                        cliente.getBalance(),
                        cliente.getValorCuota(),
                        cliente.getTipo(),
                        cliente.getStatus()

                );
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            mostrarAlerta("Error al actualizar clientes: " + e.getMessage());
            return;
        }

        lblMensaje.setText("Cobros generados para " + clientesActualizados.size() + " clientes activos.");

        // Mostrar la pestaña con los cobros creados
        mostrarPestanaCobros();
    }

    private boolean existeCobroParaMes(String mes) {
        File archivoCobros = new File(ARCHIVO_COBROS);

        if (!archivoCobros.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCobros))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 5 && partes[4].equals("cobro de " + mes)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al verificar cobros existentes: " + e.getMessage());
        }
        return false;
    }

    private void mostrarPestanaCobros() throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.PestanaDeCobros(new Stage());
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void Salir(ActionEvent actionEvent) {
        Stage stageActual = (Stage) BtSalir.getScene().getWindow();
        stageActual.close();
    }

    private String obtenerNombreMes(int mes) {
        String[] meses = {"enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        return meses[mes - 1];
    }

    private int obtenerUltimoIdCobro() {
        File archivoCobros = new File(ARCHIVO_COBROS);
        int ultimoId = 0;

        if (!archivoCobros.exists()) {
            return ultimoId;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCobros))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length > 0) {
                        try {
                            int idActual = Integer.parseInt(partes[0]);
                            if (idActual > ultimoId) {
                                ultimoId = idActual;
                            }
                        } catch (NumberFormatException e) {
                            mostrarAlerta("Error en el ID del cobro: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de cobros: " + e.getMessage());
        }
        return ultimoId;
    }

    @FXML
    private void Imprimir()
    {
        VolanteCobrosController imprimir = new VolanteCobrosController();
        imprimir.imprimirPDF();
    }
}