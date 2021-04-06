package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Model.Enums.SelectionPolicy;
import Model.Simulation.SimulationManager;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InputDataController implements Initializable {

    private final String TEXT_FIELD_STYLE_ERROR = "-fx-text-inner-color: white; -jfx-focus-color: #f0230c; -jfx-unfocus-color: #f0230c";

    @FXML private JFXTextField txtNoOfClients;
    @FXML private JFXTextField txtNoOfQueues;
    @FXML private JFXTextField txtSimulationInterval;
    @FXML private JFXTextField txtMinArrival;
    @FXML private JFXTextField txtMaxArrival;
    @FXML private JFXTextField txtMinService;
    @FXML private JFXTextField txtMaxService;
    @FXML private ComboBox<String> comboSelectionPolicy;
    private final ObservableList<String> selectionPolicies = FXCollections.observableArrayList("Smallest Number of Clients", "Shortest Waiting Time");

    private void createAlert(String errorMessage) {
        Alert alert =  new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Corrupt input data");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.show();
    }

    private void createAlertForTextField(TextField txtField, String errorMessage) {
        txtField.setStyle(TEXT_FIELD_STYLE_ERROR);
        createAlert(errorMessage);
    }

    private SimulationManager checkInputData(SimulationController simulationController) {
        setSimpleTextFieldStyle();
        if (txtNoOfClients.getText().isEmpty() || txtNoOfQueues.getText().isEmpty() || txtSimulationInterval.getText().isEmpty() || txtMinArrival.getText().isEmpty() ||
        txtMaxArrival.getText().isEmpty() || txtMinService.getText().isEmpty() || txtMaxService.getText().isEmpty() || comboSelectionPolicy.getSelectionModel().getSelectedItem() == null) {
            createAlert("Please fill in all of the required data.");
            return null;
        }

        int noOfQueues, noOfClients, simulationInterval, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime;
        SelectionPolicy selectionPolicy;
        try {
            noOfClients = Integer.parseInt(txtNoOfClients.getText());
            noOfQueues = Integer.parseInt(txtNoOfQueues.getText());
            simulationInterval = Integer.parseInt(txtSimulationInterval.getText());
            minArrivalTime = Integer.parseInt(txtMinArrival.getText());
            maxArrivalTime = Integer.parseInt(txtMaxArrival.getText());
            minServiceTime = Integer.parseInt(txtMinService.getText());
            maxServiceTime = Integer.parseInt(txtMaxService.getText());
            selectionPolicy = comboSelectionPolicy.getSelectionModel().getSelectedItem().equals("Shortest Queue") ? SelectionPolicy.SHORTEST_QUEUE : SelectionPolicy.SHORTEST_TIME;
        } catch (NumberFormatException e) {
            createAlert("The input data must contain only digits!");
            return null;
        }

        if (noOfClients <= 0) {
            createAlertForTextField(txtNoOfClients, "The number of clients must be greater than zero.");
        } else if (noOfQueues <= 0) {
            createAlertForTextField(txtNoOfQueues, "The number of queues must be greater than zero.");
        } else if (simulationInterval <= 0) {
            createAlertForTextField(txtSimulationInterval, "The simulation interval must be greater than zero.");
        } else if (minArrivalTime <= 0) {
            createAlertForTextField(txtMinArrival, "The minimum arrival time must be greater than zero.");
        } else if (maxArrivalTime <= 0) {
            createAlertForTextField(txtMaxArrival, "The maximum arrival time must be greater than zero.");
        } else if (maxArrivalTime <= minArrivalTime) {
            createAlertForTextField(txtMinArrival, "The maximum arrival time cannot be less than or equal to the minimum arrival time.");
            txtMaxArrival.setStyle(TEXT_FIELD_STYLE_ERROR);
        } else if (minServiceTime <= 0) {
            createAlertForTextField(txtMinService, "The minimum service time must be greater than zero.");
        } else if (maxServiceTime <= 0) {
            createAlertForTextField(txtMaxService, "The maximum service time must be greater than zero.");
        } else if (maxServiceTime <= minServiceTime) {
            createAlertForTextField(txtMinService, "The maximum service time cannot be less than or equal to the minimum arrival time.");
            txtMaxService.setStyle(TEXT_FIELD_STYLE_ERROR);
        } else {
            return new SimulationManager(simulationInterval, minServiceTime, maxServiceTime, minArrivalTime, maxArrivalTime, noOfQueues, noOfClients, selectionPolicy, simulationController);
        }

        return null;
    }

    private void setSimpleTextFieldStyle() {
        String TEXT_FIELD_STYLE = "-fx-prompt-text-fill: white;  -fx-text-fill: white; -fx-text-inner-color: white; -jfx-focus-color: #3768f8; -jfx-unfocus-color: #ffffff";
        txtNoOfClients.setStyle(TEXT_FIELD_STYLE);
        txtNoOfQueues.setStyle(TEXT_FIELD_STYLE);
        txtSimulationInterval.setStyle(TEXT_FIELD_STYLE);
        txtMinArrival.setStyle(TEXT_FIELD_STYLE);
        txtMaxArrival.setStyle(TEXT_FIELD_STYLE);
        txtMinService.setStyle(TEXT_FIELD_STYLE);
        txtMaxService.setStyle(TEXT_FIELD_STYLE);
    }

    @FXML
    private void loadSceneAndSendSimulationData() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/QueueSimulation.fxml"));
            Parent root = loader.load();
            SimulationController simulationController = loader.getController();
            SimulationManager simulationManager = checkInputData(simulationController);
            if (simulationManager != null) {
                simulationController.transferSimulationData(simulationManager);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Queue Simulation");
                stage.show();

                stage.setOnCloseRequest(event -> simulationController.interruptBackgroundThread());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setSimpleTextFieldStyle();
        comboSelectionPolicy.setItems(selectionPolicies);
    }
}
