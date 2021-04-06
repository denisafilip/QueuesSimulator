package Model.Simulation;

import Model.Task.Client;
import Model.Server.Queue;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
@Setter
public class SimulationStatistics {
    private static final Logger logger = LogManager.getLogger(SimulationStatistics.class);
    private AtomicInteger peakHour;
    private AtomicInteger totalWaitingTime;
    private AtomicInteger maximumNoOfClientsInQueuesEver;
    private volatile double averageWaitingTime;
    private volatile double averageServiceTime;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public SimulationStatistics() {
        peakHour = new AtomicInteger(0);
        totalWaitingTime = new AtomicInteger(0);
        maximumNoOfClientsInQueuesEver = new AtomicInteger(0);
    }

    public synchronized void addToWaitingTime(Client c) {
        this.totalWaitingTime.addAndGet(c.getWaitingTimeInQueue().get());
    }

    public synchronized void computeAverageWaitingTime(int totalNumberOfClients) {
        averageWaitingTime = (double) totalWaitingTime.get() / totalNumberOfClients;
    }

    public synchronized void getTotalClientsCurrentlyInQueues(List<Queue> queues, int currentTime) {
        lock.writeLock().lock();
        try {
            AtomicInteger totalNumberOfClients = new AtomicInteger(0);
            for (Queue q : queues) {
                totalNumberOfClients.addAndGet(q.getClients().size());
            }
            computePeakHour(currentTime, totalNumberOfClients);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public synchronized void computePeakHour(int currentTime, AtomicInteger currentNumberOfClients) {
        if (maximumNoOfClientsInQueuesEver.get() < currentNumberOfClients.get()) {
            maximumNoOfClientsInQueuesEver.set(currentNumberOfClients.get());
            peakHour.set(currentTime);
        }
    }

    public synchronized void computeAverageServiceTime(List<Client> clients) {
        lock.writeLock().lock();
        try {
            double sum = clients.stream().mapToDouble(Client::getServiceTime).sum();
            averageServiceTime = sum / clients.size();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void logSimulationStatistics() {
        logger.info(this);
    }

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return "Average waiting time: " + formatter.format(averageWaitingTime) + ";\n" + "Average service time: " + formatter.format(averageServiceTime) + ";\n" + "Peak hour: " + peakHour + ".\n";
    }

}
