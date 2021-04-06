package Model.Simulation;

import java.util.ArrayList;
import java.util.List;

import Controller.SimulationController;
import Model.Task.Client;
import Model.Enums.SelectionPolicy;
import Model.Server.Queue;
import Model.Strategy.ConcreteStrategyQueue;
import Model.Strategy.ConcreteStrategyTime;
import Model.Strategy.Strategy;
import lombok.*;

@Getter
public final class Scheduler {

    private final List<Queue> queues;
    private final Strategy strategy;

    public Scheduler(int noOfQueues, SelectionPolicy selectionPolicy, SimulationController simulationController) {
        queues = new ArrayList<>();
        for (int i = 0; i < noOfQueues; i++) {
            Queue q = new Queue(i + 1, simulationController);
            queues.add(q);
        }
        this.strategy = changeStrategy(selectionPolicy);
    }

    public void openQueues() {
        for (Queue q : queues) {
            Thread t = new Thread(q);
            t.start();
        }
    }

    public Strategy changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            return new ConcreteStrategyQueue();
        }
        if (policy == SelectionPolicy.SHORTEST_TIME) {
            return new ConcreteStrategyTime();
        }
        return null;
    }

    public Queue getShortestQueueBySize() {
        Queue shortestQueue = queues.get(0);
        for (Queue q : queues) {
            if (q.getClients().size() < shortestQueue.getClients().size()) {
                shortestQueue = q;
            }
        }
        return shortestQueue;
    }

    public Queue getShortestQueueByTime() {
        Queue shortestQueue = queues.get(0);
        for (Queue q : queues) {
            if (q.getWaitingPeriod().get() < shortestQueue.getWaitingPeriod().get()) {
                shortestQueue = q;
            }
        }
        return shortestQueue;
    }

    public Queue getShortestQueue(SelectionPolicy selectionPolicy) {
        return (selectionPolicy == SelectionPolicy.SHORTEST_QUEUE) ? getShortestQueueBySize() : getShortestQueueByTime();
    }

    public void dispatchClient(Client c) {
        strategy.addClient(this, c);
    }

    public boolean checkIfClientsInQueues() {
        for (Queue q : queues) {
            if (!q.getClients().isEmpty()) return true;
        }
        return false;
    }

    public Queue getQueueByID(int ID) {
        return queues.stream().filter(queue -> ID == queue.getQueueID()).findFirst().orElse(null);
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Queue q : queues) {
            str.append(q).append('\n');
        }
        return str.toString();
    }

    public void stop() {
        for (Queue q : queues) {
            q.stop();
        }
    }
}
