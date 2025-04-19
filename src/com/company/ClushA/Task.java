package com.company.ClushA;

public class Task {
    public int id;
    public double arrivalTime;
    public double serviceTime;
    public double departureTime;

    // ..
    public double queueEnterTime; // Время попадания в очередь
    public double queueExitTime;  // Время выхода из очереди

    public double getQueueWaitTime() {
        return queueExitTime - queueEnterTime;
    }
}

