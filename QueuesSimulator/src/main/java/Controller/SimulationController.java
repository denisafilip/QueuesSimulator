package Controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import Model.Task.Client;
import Model.Server.Queue;
import Model.Simulation.SimulationManager;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SimulationController{

    @FXML private Button btnSimulation;
    @FXML private Button btnSimStatistics;
    @FXML private Button btnBack;
    @FXML private JFXTextField txtCurrentTime;
    @FXML private HBox hBoxWaiting;
    @FXML private GridPane gridPaneQueues;
    private ConcurrentMap<Queue, VBox> vBoxQueues;
    private SimulationManager simulationManager;
    private Thread simulationThread;
    private ConcurrentMap<Client, Group> waitingClients;
    private ConcurrentMap<Client, Label> serviceTimeLabels;

    public void interruptBackgroundThread() {
        simulationThread.interrupt();
    }

    @FXML
    private void goToInputWindow() {
        simulationThread.interrupt();
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    @FXML
    private synchronized void startSimulation() {
        createQueues();
        createWaitingQueue(simulationManager.getGeneratedClients());

        simulationThread.start();
    }

    @FXML
    private synchronized void disableButton() {
        btnSimulation.setDisable(true);
    }

    public void transferSimulationData(SimulationManager sim) {
        this.simulationManager = sim;
        simulationThread = new Thread(simulationManager);
    }

    private synchronized void createQueues() {
        int index = 0;
        vBoxQueues = new ConcurrentHashMap<>();
        for (Queue q : simulationManager.getScheduler().getQueues()) {

            createQueue(q, index);
            index++;
        }
    }

    private synchronized void createWaitingQueue(List<Client> clients) {
        waitingClients = new ConcurrentHashMap<>();
        serviceTimeLabels = new ConcurrentHashMap<>();
        for (Client c : clients) {
            waitingClients.put(c, createWaitingClient(c));
        }
    }

    private synchronized void createQueue(Queue queue, int index) {
        Label lbl = new Label("Queue " + queue.getQueueID());
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        gridPaneQueues.add(lbl, index, 0);
        GridPane.setHalignment(lbl, HPos.CENTER);
        GridPane.setValignment(lbl, VPos.TOP);

        VBox vBoxQueue = new VBox();
        vBoxQueue.setPrefWidth(100);
        vBoxQueue.setPrefHeight(1480);
        vBoxQueue.setStyle("-fx-border-color: #000000");
        vBoxQueue.setFillWidth(false);

        vBoxQueues.put(queue, vBoxQueue);
        gridPaneQueues.add(vBoxQueue, index, 0);
        GridPane.setValignment(vBoxQueue, VPos.BOTTOM);
        GridPane.setHalignment(vBoxQueue, HPos.CENTER);
        GridPane.setMargin(vBoxQueue, new Insets(20, 0, 0, 0));
    }

    private synchronized void createClientInQueue(VBox queue, Client c) {
        Group client = new Group();

        client.getChildren().add(createClientImageView(11, 4));
        client.getChildren().add(createClientIdLabel(c, -15, 13));
        client.getChildren().add(createClientServiceTimeLabel(c, 44, 12));
        queue.getChildren().add(client);

        waitingClients.put(c, client);
        queue.setStyle("-fx-border-color: black");
        VBox.setMargin(client, new Insets(2, 0, 0, 0));
    }

    private synchronized ImageView createClientImageView(int x, int y) {
        File file = new File("D:\\Documents\\UTCN\\Anul II\\SEM2\\PT\\PT2021_30422_Filip_Denisa_Assignment_2\\PT2021_30422_Filip_Denisa_Assignment_2\\src\\main\\java\\View\\images\\person.jpg");
        Image image = new Image(file.toURI().toString());
        ImageView clientPicture = new ImageView();
        clientPicture.setImage(image);
        clientPicture.setLayoutX(x);
        clientPicture.setLayoutY(y);
        clientPicture.setFitWidth(39);
        clientPicture.setFitHeight(35);
        return clientPicture;
    }

    private synchronized Label createClientDataLabel(int x, int y, Label lbl) {
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        lbl.setAlignment(Pos.CENTER);
        lbl.setPrefWidth(38);
        lbl.setPrefHeight(17);
        lbl.setLayoutX(x);
        lbl.setLayoutY(y);
        return lbl;
    }

    private Label createClientIdLabel(Client client, int x, int y) {
        Label clientId = new Label(String.valueOf(client.getID()));
        return createClientDataLabel(x, y, clientId);
    }

    private Label createClientServiceTimeLabel(Client client, int x, int y) {
        Label serviceTime = new Label(String.valueOf(client.getServiceTime()));
        serviceTimeLabels.put(client, serviceTime);
        return createClientDataLabel(x, y, serviceTime);
    }

    private Label createClientArrivalTimeLabel(Client client, int x, int y) {
        Label arrivalTime = new Label(String.valueOf(client.getArrivalTime()));
        return createClientDataLabel(x, y, arrivalTime);
    }

    private synchronized Group createWaitingClient(Client c) {
        Group client = new Group();

        client.getChildren().add(createClientImageView(1, 49));
        client.getChildren().add(createClientIdLabel(c, 2, -2));
        client.getChildren().add(createClientArrivalTimeLabel(c, 2, 15));
        client.getChildren().add(createClientServiceTimeLabel(c, 2, 33));

        hBoxWaiting.getChildren().add(client);
        return client;
    }

    public synchronized void removeClientsFromWaitingQueue(Client c) {
            hBoxWaiting.getChildren().remove(waitingClients.get(c));
            waitingClients.remove(c);
    }

    public synchronized void addClientToQueue(Queue q, Client c) {
        Platform.runLater(() -> {
            removeClientsFromWaitingQueue(c);
            createClientInQueue(vBoxQueues.get(q), c);
        });
    }

    public synchronized void removeClientFromQueue(Queue q, Client c) {
       Platform.runLater(() -> {
           vBoxQueues.get(q).getChildren().remove(waitingClients.get(c));
           if (vBoxQueues.get(q).getChildren().isEmpty()) {
               vBoxQueues.get(q).setStyle("-fx-border-color: red");
           }
           waitingClients.remove(c);
       });

    }

    public synchronized void decreaseServiceTimeLabel(Client c) {
        Platform.runLater(() -> serviceTimeLabels.get(c).setText(String.valueOf(c.getServiceTime())));
    }

    public synchronized void setLblCurrentTime(String s) {
        Platform.runLater(() -> txtCurrentTime.setText(s));
    }

    @FXML
    private void loadSimulationStatisticsScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SimulationStatistics.fxml"));
            Parent root = loader.load();

            SimulationStatisticsController simulationStatisticsController = loader.getController();
            if (simulationManager != null) {
                simulationStatisticsController.transferSimulationStatistics(simulationManager.getSimulationStatistics());
                simulationStatisticsController.writeToTextFields();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Simulation Statistics");
                stage.show();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void enableSimulationStatisticsButton() {
        btnSimStatistics.setDisable(false);
    }
}