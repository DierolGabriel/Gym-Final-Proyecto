package Controllers_y_Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Mainlogin extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        // Cargar el FXML sin especificar el tamaño de la escena (ajustará al tamaño del AnchorPane)
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");

        // Establecer el tamaño de la ventana según el tamaño de AnchorPane en FXML (1200x800)
        stage.setWidth(1200);  // Establecer el ancho
        stage.setHeight(800);  // Establecer la altura
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args)
    {
        launch();
    }

    public static class HelloApplication extends Application
    {
        @Override
        public void start(@NotNull Stage stage) throws IOException
        {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Menu.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setTitle("Menu");
            stage.setScene(scene);
            stage.show();
        }
    }
}