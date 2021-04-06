package Model.Server;

import Controller.SimulationController;
import Model.Task.Client;
import Model.Enums.ClientStatus;
import Model.Enums.QueueStatus;
import lombok.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Queue implements Runnable {

    private int queueID;
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;
    private volatile QueueStatus status;
    private AtomicInteger totalClients;
    private volatile SimulationController simulationController;

    public Queue(int queueID, SimulationController simulationController) {
        this.clients = new LinkedBlockingQueue<>();
        this.status = QueueStatus.OPEN;
        this.queueID = queueID;
        this.waitingPeriod = new AtomicInteger(0);
        this.totalClients = new AtomicInteger(0);
        this.simulationController = simulationController;
    }

    public void addClient(Client newClient) {

        this.clients.add(newClient);
        this.waitingPeriod.addAndGet(newClient.getServiceTime());
        incrementTotalNumberOfClients();
        newClient.setWaitingTimeInQueue(this.waitingPeriod);
        simulationController.addClientToQueue(this, newClient);
        newClient.setStatus(ClientStatus.IN_QUEUE);
    }

    public void incrementTotalNumberOfClients() {
        this.totalClients.incrementAndGet();
    }

    private void removeClient() {
        Client c = clients.remove();
        simulationController.removeClientFromQueue(this, c);
        c.setStatus(ClientStatus.SERVED);
    }

    private void serveClient() throws InterruptedException {
        Client c = clients.peek();
        assert c != null;
        if (c.getServiceTime() == 1) {
            removeClient();
            Thread.sleep(1000);
        } else {
            c.decreaseServiceTimeEverySecond(this, this.simulationController);
        }
    }

    @Override
    public void run() {
        while (status == QueueStatus.OPEN) {
            if (!clients.isEmpty()) {
                try {
                    serveClient();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("The thread was interrupted.");
                    return;
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("The thread was interrupted.");
                    return;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Queue " + this.queueID + ": ");
        if (this.clients.isEmpty()) {
            str.append("closed");
        } else {
            for (Client c : this.clients) {
                str.append(c.toString()).append("; ");
            }
        }
        return str.toString();
    }

    public void stop() {
        this.status = QueueStatus.CLOSED;
    }
}
