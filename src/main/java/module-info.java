module com.example.assignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.programming.assignment to javafx.fxml;
    exports com.programming.assignment;
}