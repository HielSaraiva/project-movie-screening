module edu.ifce {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    opens edu.ifce.controller to javafx.fxml;

    opens edu.ifce to javafx.graphics, javafx.fxml;

    exports edu.ifce;
    exports edu.ifce.controller;
    exports edu.ifce.function;
    exports edu.ifce.thread;
}