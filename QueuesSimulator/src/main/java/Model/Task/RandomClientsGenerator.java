package Model.Task;

import Model.Enums.ClientStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class RandomClientsGenerator {
    private final List<Client> clients;

    public RandomClientsGenerator(int numberOfClients, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime) {
        clients = new ArrayList<>();
        for (int i = 1; i <= numberOfClients; i++) {
            clients.add(generateRandomClient(i, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime));
        }
    }

    private Client generateRandomClient(int clientID, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime) {
        int arrivalTime = generateRandomArrivalTime(minArrivalTime, maxArrivalTime);
        int serviceTime = generateRandomServiceTime(minServiceTime, maxServiceTime);

        return new Client(clientID, arrivalTime, serviceTime, ClientStatus.WAITING);
    }

    private int generateRandomArrivalTime(int minArrivalTime, int maxArrivalTime) {
        Random rand = new Random();
        return rand.nextInt((maxArrivalTime - minArrivalTime) + 1) + minArrivalTime;
    }

    private int generateRandomServiceTime(int minServiceTime, int maxServiceTime) {
        Random rand = new Random();
        return rand.nextInt((maxServiceTime - minServiceTime) + 1) + minServiceTime;
    }
}
