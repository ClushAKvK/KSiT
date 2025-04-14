package com.company.Template;

public class SimulationResult {
    public int tasksProcessed_1;
    public double busyTime;
    public double totalTime;
    public double totalDelay;

    public SimulationResult(int tasksProcessed, double busyTime, double totalTime, double totalDelay) {
        this.tasksProcessed_1 = tasksProcessed;
        this.busyTime = busyTime;
        this.totalTime = totalTime;
        this.totalDelay = totalDelay;
    }
}

