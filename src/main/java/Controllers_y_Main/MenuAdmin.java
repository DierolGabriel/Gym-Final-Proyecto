package Controllers_y_Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuAdmin extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("MenuAdmin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1098, 815);
        stage.setTitle("Menu");
        stage.setScene(scene);
        stage.show();
    }

    public void Entrenador(Stage stage) throws IOException
    {
    FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Entrenador.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 477, 641);
        stage.setTitle("Entrenador");
        stage.setScene(scene);
        stage.show();
    }

    public void Usuarios(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Usuarios.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Usuarios");
        stage.setScene(scene);
        stage.show();
        stage.alwaysOnTopProperty();
    }
    public void Localizacion(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Localizacion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 601, 212);
        stage.setTitle("Localizacion");
        stage.setScene(scene);
        stage.show();
    }
    public void Salas(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Salas.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 358);
        stage.setTitle("Salas");
        stage.setScene(scene);
        stage.show();
    }

    public void Actividades(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Actividades.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 477, 641);
        stage.setTitle("Actividades");
        stage.setScene(scene);
        stage.show();
    }
    public void EstadoR(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("EstadoR.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 464, 373);
        stage.setTitle("Estado");
        stage.setScene(scene);
        stage.show();
    }
    public void Clientes(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Clientes.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1146, 582);
        stage.setTitle("Clientes");
        stage.setScene(scene);
        stage.show();
    }

    public void ReservaAct(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Reserva Actividades.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 691, 510);
        stage.setTitle("Reserva de Actividades");
        stage.setScene(scene);
        stage.show();
    }

    public void Reserva(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Reserva.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Reserva");
        stage.setScene(scene);
        stage.show();
    }

    public void Horarios(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Horarios_Actividades.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 507, 400);
        stage.setTitle("Horarios de actividades");
        stage.setScene(scene);
        stage.show();
    }

    public void menu(Stage stage) throws  IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1098, 815);
        stage.setTitle("Menu");
        stage.setScene(scene);
        stage.show();
    }

    public void ConUsuario(Stage stage) throws  IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("ConUsuarios.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 824, 609);
        stage.setTitle("Consulta de Usuario");
        stage.setScene(scene);
        stage.show();
    }
    public void ConEntrenador(Stage stage) throws  IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("ConEntrenador.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 824, 609);
        stage.setTitle("Consulta de Entrenador");
        stage.setScene(scene);
        stage.show();
    }

    public void ConLocalizacion(Stage stage) throws  IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("Con Localizacion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 738, 542);
        stage.setTitle("Consulta de Localizacion");
        stage.setScene(scene);
        stage.show();
    }

    public void ConActividades(Stage stage) throws  IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("ConActividades.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 738, 542);
        stage.setTitle("Consulta de Actividades");
        stage.setScene(scene);
        stage.show();
    }

    public void ConHorarios(Stage stage) throws  IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("ConHorarios.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 824, 609);
        stage.setTitle("Consulta de Horarios");
        stage.setScene(scene);
        stage.show();
    }

    public void ConSalas(Stage stage) throws  IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("ConSalas.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 824, 609);
        stage.setTitle("Consulta de Salas");
        stage.setScene(scene);
        stage.show();
    }

    public void ConClientes(Stage stage) throws  IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Mainlogin.HelloApplication.class.getResource("ConClientes.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1123, 723);
        stage.setTitle("Consulta de Clientes");
        stage.setScene(scene);
        stage.show();
    }



}