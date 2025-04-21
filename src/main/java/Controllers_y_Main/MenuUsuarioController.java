package Controllers_y_Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuUsuarioController {

    @FXML
    private Button ActualizaCuota;

    @FXML
    private Button AdCuotas;

    @FXML
    private Button ConActividades;

    @FXML
    private Button ConClientes;

    @FXML
    private Button ConCobro;

    @FXML
    private Button ConCuota;

    @FXML
    private Button ConEntrenador;

    @FXML
    private Button ConHorarios;

    @FXML
    private Button ConLocalización;

    @FXML
    private Button ConSalas;

    @FXML
    private Button ConUsuarios;

    @FXML
    private TitledPane Consultas;

    @FXML
    private Button GenerarCobro;

    @FXML
    private Button ManActividades;

    @FXML
    private Button ManClientes;

    @FXML
    private Button ManEntrenador;

    @FXML
    private Button ManEstados;

    @FXML
    private Button ManHorarios;

    @FXML
    private Button ManLocalizacion;

    @FXML
    private Button ManReserva;

    @FXML
    private Button ManReservaAc;

    @FXML
    private Button ManSalas;


    @FXML
    private TitledPane Mantenimiento;

    @FXML
    private TitledPane Movimientos;

    @FXML
    private TitledPane Procesos;

    @FXML
    void ConCuota(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ConCuota(new Stage());
    }

    @FXML
    void ConCobro(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ConCobro(new Stage());
    }

    @FXML
    void ConActividades(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ConActividades(new Stage());
    }

    @FXML
    void ConClientes(ActionEvent event) throws IOException
    {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ConClientes(new Stage());
    }

    @FXML
    void ConEntrenador(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ConEntrenador(new Stage());
    }

    @FXML
    void ConHorarios(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ConHorarios(new Stage());
    }

    @FXML
    void ConLocalización(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ConLocalizacion(new Stage());
    }

    @FXML
    void ConSalas(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ConSalas(new Stage());
    }

    @FXML
    void ConUsuarios(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ConUsuario(new Stage());
    }

    @FXML
    void ManActividades(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.Actividades(new Stage());
    }

    @FXML
    void ManClientes(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.Clientes(new Stage());
    }

    @FXML
    void ManEntrenador(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.Entrenador(new Stage());
    }

    @FXML
    void ManEstados(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.EstadoR(new Stage());
    }

    @FXML
    void ManHorarios(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.Horarios(new Stage());
    }

    @FXML
    void ManLocalizacion(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.Localizacion(new Stage());
    }

    @FXML
    void ManReserva(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.Reserva(new Stage());
    }

    @FXML
    void ManReservaAc(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ReservaAct(new Stage());
    }


    @FXML
    void ManSalas(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.Salas(new Stage());
    }

    @FXML
    void  GenerarCobro(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.GenerarCobro(new Stage());
    }
    @FXML
    void  ActualizaCuota(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.ActualizaCuota(new Stage());
    }

    @FXML
    void  AdCuotas(ActionEvent event) throws IOException {
        MenuAdmin menuAdmin = new MenuAdmin();
        menuAdmin.AdCuotas(new Stage());
    }

}
