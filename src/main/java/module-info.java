module edu.ifce {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.ifce to javafx.fxml;
    exports edu.ifce;
}
