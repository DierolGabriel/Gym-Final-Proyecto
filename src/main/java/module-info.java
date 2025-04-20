module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires annotations;
    requires org.apache.pdfbox;

    opens Controllers_y_Main to javafx.fxml;
    exports Controllers_y_Main;
    exports Consultas;
    opens Consultas to javafx.fxml;
}