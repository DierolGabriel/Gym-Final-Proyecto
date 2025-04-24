package Controllers_y_Main;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.event.KeyEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Cuotacontroller {
    @FXML
    private TableView<Cobro> tablaCobros;
    @FXML
    private TableColumn<Cobro, Integer> IDCobro;
    @FXML
    private TableColumn<Cobro, String> FechaCobro;
    @FXML
    private TableColumn<Cobro, String> IDCliente;
    @FXML
    private TableColumn<Cobro, Double> ValorCobro;
    @FXML
    private TableColumn<Cobro, String> ConceptoCobro;
    @FXML
    private TableColumn<Cobro, Boolean> Status;
    @FXML
    private TableColumn<Cobro, Boolean> Seleccionado;

    @FXML
    private DatePicker Fecha;
    @FXML
    private Button Guardar;
    @FXML
    private TextField IDCuota;
    @FXML
    private ComboBox<String> Idcliente;
    @FXML
    private Button Limpiar;
    @FXML
    private TextField Nombre;
    @FXML
    private TextField Notificador;
    @FXML
    private Button Salir;
    @FXML
    private TextField Valor;

    private static final String ARCHIVO_COBROS = "Cobro.txt";
    private static final String ARCHIVO_CLIENTES = "Clientes.txt";
    private static final String ARCHIVO_CUOTAS = "Cuota.txt";
    private boolean modificando = false;
    private ObservableList<Cobro> cobrosData = FXCollections.observableArrayList();

    public static class Cobro {
        private final IntegerProperty idCobro;
        private final StringProperty fechaCobro;
        private final StringProperty idCliente;
        private final DoubleProperty valorCobro;
        private final StringProperty concepto;
        private final BooleanProperty status;
        private final BooleanProperty seleccionado;

        public Cobro(int idCobro, String fechaCobro, String idCliente, double valorCobro, String concepto, boolean status) {
            this.idCobro = new SimpleIntegerProperty(idCobro);
            this.fechaCobro = new SimpleStringProperty(fechaCobro);
            this.idCliente = new SimpleStringProperty(idCliente);
            this.valorCobro = new SimpleDoubleProperty(valorCobro);
            this.concepto = new SimpleStringProperty(concepto);
            this.status = new SimpleBooleanProperty(status);
            this.seleccionado = new SimpleBooleanProperty(false);
        }

        // Getters para propiedades
        public IntegerProperty idCobroProperty() { return idCobro; }
        public StringProperty fechaCobroProperty() { return fechaCobro; }
        public StringProperty idClienteProperty() { return idCliente; }
        public DoubleProperty valorCobroProperty() { return valorCobro; }
        public StringProperty conceptoProperty() { return concepto; }
        public BooleanProperty statusProperty() { return status; }
        public BooleanProperty seleccionadoProperty() { return seleccionado; }

        // Getters normales
        public int getIdCobro() { return idCobro.get(); }
        public String getFechaCobro() { return fechaCobro.get(); }
        public String getIdCliente() { return idCliente.get(); }
        public double getValorCobro() { return valorCobro.get(); }
        public String getConcepto() { return concepto.get(); }
        public boolean isStatus() { return status.get(); }
        public boolean isSeleccionado() { return seleccionado.get(); }

        // Setters
        public void setStatus(boolean status) { this.status.set(status); }
        public void setSeleccionado(boolean seleccionado) {
            this.seleccionado.set(seleccionado);
            // Eliminamos la línea que cambiaba el status
        }
    }

    @FXML
    public void initialize() {
        IDCobro.setCellValueFactory(new PropertyValueFactory<>("idCobro"));
        FechaCobro.setCellValueFactory(new PropertyValueFactory<>("fechaCobro"));
        IDCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        ValorCobro.setCellValueFactory(new PropertyValueFactory<>("valorCobro"));
        ConceptoCobro.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        Status.setCellValueFactory(new PropertyValueFactory<>("status"));


        Seleccionado.setCellValueFactory(param -> param.getValue().seleccionadoProperty());
        Seleccionado.setCellFactory(CheckBoxTableCell.forTableColumn(Seleccionado));


        tablaCobros.setRowFactory(tv -> {
            TableRow<Cobro> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Cobro cobro = row.getItem();
                    cobro.setSeleccionado(!cobro.isSeleccionado());
                    calcularTotalSeleccionado();
                }
            });
            return row;
        });


        cobrosData.addListener((ListChangeListener<Cobro>) change -> {
            while (change.next()) {
                if (change.wasUpdated()) {
                    calcularTotalSeleccionado();
                }
            }
        });


        tablaCobros.setEditable(true);
        Seleccionado.setOnEditCommit(event -> {
            Cobro cobro = event.getRowValue();
            cobro.setSeleccionado(event.getNewValue());
            calcularTotalSeleccionado();
        });


        IDCuota.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                validarIdCuota(newValue);
            } else {
                limpiarCampos(true);
                desactivarCampos();
                Notificador.clear();
                modificando = false;
            }
        });

        Idcliente.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                cargarNombreCliente(newValue);
                cargarCobrosCliente(newValue);
            }
        });
        cargarClientes();


        Fecha.setValue(LocalDate.now());
    }

    private void calcularTotalSeleccionado() {
        double total = 0.0;
        for (Cobro cobro : cobrosData) {
            if (cobro.isSeleccionado()) {
                total += cobro.getValorCobro();
            }
        }
        Valor.setText(String.format("%.2f", total));
    }
    private void validarIdCuota(String idCuota) {
        File archivo = new File(ARCHIVO_CUOTAS);

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
                if (partes.length >= 5 && partes[0].equals(idCuota) && partes[4].equals("false"))
                {
                    cargarDatosCuota(partes);
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
                modificando = false;
            }


        } catch (IOException e) {
            mostrarAlerta("Error al leer el archivo de cuotas: " + e.getMessage());
        }
    }

    private void cargarDatosCuota(String[] datos) {
        if (datos.length >= 4) {
            Idcliente.setValue(datos[1]);
            Valor.setText(datos[2]);
            Fecha.setValue(LocalDate.parse(datos[3], DateTimeFormatter.ofPattern("d/M/yyyy")));
        }
    }

    private void cargarClientes() {
        File archivo = new File(ARCHIVO_CLIENTES);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            Idcliente.getItems().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 14 && partes[12].equals("Socio Activo") && partes[13].equals("Activo"))
                    {
                        Idcliente.getItems().add(partes[0].trim());
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar Clientes: " + e.getMessage());
        }
    }

    private void cargarNombreCliente(String idCliente) {
        File archivo = new File(ARCHIVO_CLIENTES);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 2 && partes[0].equals(idCliente)) {
                        Nombre.setText(partes[1].trim());
                        break;
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar nombre del cliente: " + e.getMessage());
        }
    }

    private void cargarCobrosCliente(String idCliente) {
        File archivo = new File(ARCHIVO_COBROS);
        cobrosData.clear();

        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 6 && partes[2].equals(idCliente) && partes[5].equals("false"))
                    {
                        int idCobro = Integer.parseInt(partes[0]);
                        String fecha = partes[1];
                        double valor = Double.parseDouble(partes[3]);
                        String concepto = partes[4];
                        boolean status = Boolean.parseBoolean(partes[5]);

                        cobrosData.add(new Cobro(idCobro, fecha, idCliente, valor, concepto, status));
                    }
                }
            }
            tablaCobros.setItems(cobrosData);
        } catch (IOException e) {
            mostrarAlerta("Error al cargar cobros del cliente: " + e.getMessage());
        }
    }

    @FXML
    void Guardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        // Calcular el total solo de los cobros seleccionados
        double totalSeleccionado = 0.0;
        List<Cobro> cobrosSeleccionados = new ArrayList<>();
        for (Cobro cobro : cobrosData) {
            if (cobro.isSeleccionado()) {
                totalSeleccionado += cobro.getValorCobro();
                cobrosSeleccionados.add(cobro);
            }
        }

        // Verificar que el valor en el campo Valor coincida con la suma
        try {
            double valorIngresado = Double.parseDouble(Valor.getText().trim());
            if (Math.abs(valorIngresado - totalSeleccionado) > 0.01) {
                mostrarAlerta("El valor no coincide con la suma de cobros seleccionados");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("El valor debe ser un número válido");
            return;
        }

        String idCuota = IDCuota.getText().trim();
        String idCliente = Idcliente.getValue().trim();
        String valor = Valor.getText().trim();
        String fecha = Fecha.getValue().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        String Status = "false";

        String linea = String.join(":", idCuota, idCliente, valor, fecha, Status);


        File archivo = new File(ARCHIVO_CUOTAS);
        List<String> lineas = new ArrayList<>();
        boolean existe = false;

        try {
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String lineaActual;
                    while ((lineaActual = br.readLine()) != null) {
                        if (!lineaActual.trim().isEmpty()) {
                            String[] partes = lineaActual.split(":");
                            if (partes.length > 0 && partes[0].equals(idCuota)) {
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

            // Actualizar status
            actualizarCobrosSeleccionados(idCliente);

            mostrarAlerta("Cuota " + (existe ? "modificada" : "creada") + " exitosamente");
            Notificador.setText(existe ? "Modificado" : "Creado");
            limpiarCampos(true);

        } catch (IOException e) {
            mostrarAlerta("Error al guardar la cuota: " + e.getMessage());
        }
    }

    private void actualizarCobrosSeleccionados(String idCliente) throws IOException {
        File archivoCobros = new File(ARCHIVO_COBROS);
        if (!archivoCobros.exists()) return;

        List<String> lineasActualizadas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCobros))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 6 && partes[2].equals(idCliente)) {
                        // Buscar si este cobro está seleccionado
                        for (Cobro cobro : cobrosData) {
                            if (cobro.getIdCobro() == Integer.parseInt(partes[0]) && cobro.isSeleccionado()) {
                                // Actualizar solo los cobros seleccionados
                                partes[5] = "true"; // Cambiar status a true
                                linea = String.join(":", partes);
                                break;
                            }
                        }
                    }
                    lineasActualizadas.add(linea);
                }
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCobros))) {
            for (String l : lineasActualizadas) {
                bw.write(l);
                bw.newLine();
            }
        }
    }
    public void validarIdClienteManual(ActionEvent actionEvent)
    {
        if (Idcliente.isEditable() && Idcliente.getEditor().getText() != null) {
            String idIngresado = Idcliente.getEditor().getText().trim();
            if (!idIngresado.isEmpty()) {
                boolean existe = false;

                // Buscar en la lista de clientes cargados
                for (String id : Idcliente.getItems()) {
                    if (id.equals(idIngresado)) {
                        existe = true;
                        break;
                    }
                }

                // Si no está en la lista, buscar en el archivo
                if (!existe) {
                    existe = verificarIdClienteEnArchivo(idIngresado);
                }

                if (existe) {
                    Idcliente.setValue(idIngresado);
                    cargarNombreCliente(idIngresado);
                    cargarCobrosCliente(idIngresado);
                } else {
                    Nombre.clear();
                    cobrosData.clear();
                    mostrarAlerta("El ID del cliente no existe o no tiene cobros pendientes");
                }
            }
        }
    }

    private boolean verificarIdClienteEnArchivo(String idCliente) {
        File archivo = new File(ARCHIVO_CLIENTES);
        if (!archivo.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(":");
                    if (partes.length >= 14 && partes[0].equals(idCliente) &&
                            partes[12].equals("Socio Activo") && partes[13].equals("Activo")) {
                        // Si encontramos el ID y es un socio activo, lo añadimos al ComboBox
                        if (!Idcliente.getItems().contains(idCliente)) {
                            Idcliente.getItems().add(idCliente);
                        }
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error al verificar cliente: " + e.getMessage());
        }
        return false;
    }


    @FXML
    void Limpiar(ActionEvent event) {
        limpiarCampos(true);
        Notificador.clear();
        modificando = false;
    }

    @FXML
    void Salir(ActionEvent event) {
        Stage stageActual = (Stage) Notificador.getScene().getWindow();
        stageActual.close();
    }

    private void limpiarCampos(boolean incluirId) {
        if (incluirId) {
            IDCuota.clear();
        }
        Idcliente.setValue(null);
        Nombre.clear();
        Valor.clear();
        Fecha.setValue(LocalDate.now());
        cobrosData.clear();
        desactivarCampos();
    }

    private boolean validarCampos() {
        if (IDCuota.getText().trim().isEmpty() ||
                Idcliente.getValue() == null ||
                Valor.getText().trim().isEmpty() ||
                Fecha.getValue() == null) {
            mostrarAlerta("Todos los campos son obligatorios");
            return false;
        }

        try {
            int idCuota = Integer.parseInt(IDCuota.getText());
            double valor = Double.parseDouble(Valor.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("ID Cuota debe ser entero y Valor debe ser numérico");
            return false;
        }

        return true;
    }

    private void activarCampos() {
        Idcliente.setDisable(false);
        Fecha.setDisable(false);
    }

    private void desactivarCampos() {
        Idcliente.setDisable(true);
        Fecha.setDisable(true);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}