module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.apache.logging.log4j;
    requires com.jfoenix;

    opens Controller to javafx.fxml;
    opens Application to javafx.fxml;
    opens Model.Comparator to javafx.fxml;
    opens Model.Task to javafx.fxml;
    opens Model.Server to javafx.fxml;
    opens Model.Strategy to javafx.fxml;
    opens Model.Simulation to javafx.fxml;
    exports Controller;
    exports Application;
    exports Model.Comparator;
    exports Model.Task;
    exports Model.Server;
    exports Model.Simulation;
    exports Model.Strategy;
    exports Model.Enums;
}