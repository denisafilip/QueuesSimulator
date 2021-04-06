package Model.Comparator;

import Model.Task.Client;

import java.util.Comparator;

public class ClientComparator implements Comparator<Client> {
    @Override
    public int compare(Client c1, Client c2) {
        if (c1.getArrivalTime().compareTo(c2.getArrivalTime()) == 0) {
            return c1.getID().compareTo(c2.getID());
        }
        return c1.getArrivalTime().compareTo(c2.getArrivalTime());
    }
}

