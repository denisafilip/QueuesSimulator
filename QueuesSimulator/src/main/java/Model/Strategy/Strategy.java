package Model.Strategy;

import Model.Task.Client;
import Model.Simulation.Scheduler;

public interface Strategy {
    void addClient(Scheduler scheduler, Client c);
}
