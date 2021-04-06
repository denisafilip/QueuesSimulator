package Model.Strategy;

import Model.Task.Client;
import Model.Server.Queue;
import Model.Simulation.Scheduler;

public class ConcreteStrategyTime implements Strategy {

    @Override
    public void addClient(Scheduler scheduler, Client c) {
        Queue shortestQueue = scheduler.getShortestQueueByTime();
        shortestQueue.addClient(c);
    }
}
