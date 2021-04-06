package Model.Task;

import Controller.SimulationController;
import Model.Enums.ClientStatus;
import Model.Enums.QueueStatus;
import Model.Server.Queue;
import lombok.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Client {
    private final Integer ID;
    private final Integer arrivalTime;
    private Integer serviceTime;
    private AtomicInteger waitingTimeInQueue;
    private volatile ClientStatus status;
    private int finalTime;

    public Client(int ID, int arrivalTime, int serviceTime, ClientStatus status) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.status = status;
    }

    private void computeFinalTime() {
        this.finalTime = arrivalTime + serviceTime + waitingTimeInQueue.get();
    }

    public Client getClientByID(List<Client> clients, int ID) {
        return clients.stream().filter(client -> ID == getID()).findFirst().orElse(null);
    }

    public void decrementServiceTime() {
        this.serviceTime--;
    }

    public void decreaseServiceTimeEverySecond(Queue queue, SimulationController simulationController) throws InterruptedException {
        int serviceTime = this.serviceTime;
        for (int i = 0; i < serviceTime - 1 && queue.getStatus() == QueueStatus.OPEN; i++) {
            queue.getWaitingPeriod().addAndGet(-1);
            decrementServiceTime();
            simulationController.decreaseServiceTimeLabel(this);
            Thread.sleep(1000);
        }
        queue.getWaitingPeriod().addAndGet(-1);
    }

    @Override
    public String toString() {
        return "(" + this.ID + ", " + this.arrivalTime + ", " + this.serviceTime + ")";
    }
}
