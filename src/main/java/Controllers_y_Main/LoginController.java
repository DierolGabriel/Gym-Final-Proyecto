package Controllers_y_Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LoginController
    {

        @FXML
        private Button Create;

        @FXML
        private Button Login;

        @FXML
        private PasswordField contra;

        @FXML
        private TextField usuario;

        @FXML
        void Create(ActionEvent event) throws IOException {
            File arq = new File("archivo.txt");

            if (!arq.exists())
            {
                arq.createNewFile();
            }
            if(!usuario.getText().isEmpty() || !contra.getText().isEmpty())
            {
            try {
                RandomAccessFile raf = new RandomAccessFile(arq, "rw");
                raf.seek(raf.length());

                raf.writeBytes(usuario.getText());
                raf.writeBytes(":");
                raf.writeBytes((contra.getText()));
                raf.writeBytes(":");
                raf.writeBytes("0");
                raf.writeBytes("\n");

                raf.seek(0);

                raf.close();
                usuario.setText("");
                contra.setText("");
            } catch (Exception e)
            {
                javax.swing.JOptionPane.showMessageDialog(null, "Error al crear el usuario");
            }
            JOptionPane.showMessageDialog(null, "Usuario Ingresado Correctamente");
        }
            else
            {
                JOptionPane.showMessageDialog(null, "ingrese el usuario y la contraseña");
            }
        }

        @FXML
        void Login(ActionEvent event) throws Exception {
                File arq = new File("archivo.txt");
                if (!arq.exists()) {
                    arq.createNewFile();
                }
                RandomAccessFile raf = new RandomAccessFile(arq, "rw");
                raf.seek(0);

                String linea;
                boolean encontrado = false;

                while ((linea = raf.readLine()) != null)
                {
                    String[] partes = linea.split(":");
                    if (partes.length >= 3)
                    {
                        String usuarioArchivo = partes[0].trim();
                        String contrasenaArchivo = partes[1].trim();
                        int nivelArchivo= Integer.parseInt(partes[2].trim());

                        if (usuario.getText().equals(usuarioArchivo) && contra.getText().equals(contrasenaArchivo) && nivelArchivo == 0)
                        {
                            encontrado = true;
                            Stage stageActual = (Stage) usuario.getScene().getWindow();
                            stageActual.close();
                            MenuAdmin menu = new MenuAdmin();
                            menu.menu(new Stage());
                            break;
                        }
                        else if(usuario.getText().equals(usuarioArchivo) && contra.getText().equals(contrasenaArchivo) && nivelArchivo == 1)
                        {
                            encontrado = true;
                            Stage stageActual = (Stage) usuario.getScene().getWindow();
                            stageActual.close();
                            MenuAdmin menuAdmin = new MenuAdmin();
                            menuAdmin.start(new Stage());
                            break;
                        }
                    }
                }
                raf.close();

                if (!encontrado)
                {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrecta");
                }
            }
    }