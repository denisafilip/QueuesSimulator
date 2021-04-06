package Controller;

import Model.Simulation.SimulationStatistics;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class SimulationStatisticsController {
    @FXML private JFXTextField txtAvgWaitingTime;
    @FXML private JFXTextField txtAvgServiceTime;
    @FXML private JFXTextField txtPeakHour;

    private SimulationStatistics simulationStatistics;

    public void transferSimulationStatistics(SimulationStatistics simulationStatistics) {
        this.simulationStatistics = simulationStatistics;
    }

    public void writeToTextFields() {
        txtAvgServiceTime.setStyle("-fx-text-inner-color: white;");
        txtAvgServiceTime.setText(String.valueOf(simulationStatistics.getAverageServiceTime()));
        txtAvgWaitingTime.setStyle("-fx-text-inner-color: white;");
        txtAvgWaitingTime.setText(String.valueOf(simulationStatistics.getAverageWaitingTime()));
        txtPeakHour.setStyle("-fx-text-inner-color: white;");
        txtPeakHour.setText(String.valueOf(simulationStatistics.getPeakHour()));

    }

}
