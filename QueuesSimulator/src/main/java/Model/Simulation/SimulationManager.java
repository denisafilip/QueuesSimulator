package Model.Simulation;

import Controller.SimulationController;
import Model.Task.Client;
import Model.Comparator.ClientComparator;
import Model.Enums.SelectionPolicy;
import Model.Task.RandomClientsGenerator;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class SimulationManager implements Runnable {
    private final int simulationInterval;
    private final int maxServiceTime;
    private final int minServiceTime;
    private final int maxArrivalTime;
    private final int minArrivalTime;
    private final int numberOfQueues;
    private final int numberOfClients;
    private final SelectionPolicy selectionPolicy;

    //frame for displaying the simulation
    private final SimulationController simulationController;
    private final Scheduler scheduler;

    private final List<Client> generatedClients;

    private static final Logger logger = LogManager.getLogger(SimulationManager.class);

    private final SimulationStatistics simulationStatistics;

    public SimulationManager(int simulationInterval, int minServiceTime, int maxServiceTime, int minArrivalTime, int maxArrivalTime, int numberOfQueues, int numberOfClients, SelectionPolicy selectionPolicy, SimulationController simulationController) {
        this.simulationInterval = simulationInterval;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.numberOfQueues = numberOfQueues;
        this.numberOfClients = numberOfClients;

        this.selectionPolicy = selectionPolicy;
        this.simulationController = simulationController;
        this.scheduler = new Scheduler(numberOfQueues, selectionPolicy, simulationController);

        this.generatedClients = generateRandomClients();

        simulationStatistics = new SimulationStatistics();
        simulationStatistics.computeAverageServiceTime(generatedClients);

    }

    private List<Client> generateRandomClients() {
        RandomClientsGenerator randomClientsGenerator = new RandomClientsGenerator(numberOfClients, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime);
        randomClientsGenerator.getClients().sort(new ClientComparator());
        return randomClientsGenerator.getClients();
    }

    private void displayWaitingClients() {
        StringBuilder str = new StringBuilder("Waiting clients: ");
        for (Client c : generatedClients) {
            str.append(c).append("; ");
        }
        logger.info(str);
    }

    private void displayLog(int currentTime) {
        logger.info("Time: {}", currentTime);
        displayWaitingClients();
        logger.info(scheduler);
    }

    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(3000);
        int currentTime = 0;
        this.scheduler.openQueues();

        while (currentTime <= simulationInterval && (!generatedClients.isEmpty() || scheduler.checkIfClientsInQueues())) {
            simulationController.setLblCurrentTime(String.valueOf(currentTime));
            if (currentTime > 0) {
                int finalCurrentTime = currentTime;
                List<Client> clientsToBeRemoved = generatedClients.stream().filter(c -> c.getArrivalTime() == finalCurrentTime).collect(Collectors.toList());
                generatedClients.removeAll(clientsToBeRemoved);
                for (Client c : clientsToBeRemoved) {
                    scheduler.dispatchClient(c);
                    simulationStatistics.getTotalClientsCurrentlyInQueues(scheduler.getQueues(), currentTime);
                    simulationStatistics.addToWaitingTime(c);
                }
            }
            displayLog(currentTime);
            currentTime++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("The thread was interrupted.");
                return;
            }
        }
        simulationController.enableSimulationStatisticsButton();
        simulationStatistics.computeAverageWaitingTime(numberOfClients);
        displayLog(currentTime);
        simulationStatistics.logSimulationStatistics();
        stop();
    }

    public void stop() {
        scheduler.stop();
        Thread.currentThread().interrupt();
    }
}
