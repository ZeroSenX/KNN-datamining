module map.knnclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens map.knnclient to javafx.fxml;
    exports map.knnclient;
}