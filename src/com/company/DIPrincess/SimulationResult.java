package com.company.DIPrincess;

public class SimulationResult {
    public int tasksProcessed;
    public double busyTime;
    public double totalTime;
    public double totalDelay;
    public int TotalCountQueue;
    public int TotalCountQueueInLn;
    public int depaturesCount;
    public int depaturesCountAout;

    public SimulationResult(int tasksProcessed, double busyTime, double totalTime, double totalDelay,
                            int TotalCountQueue, int TotalCountQueueInLn, int depaturesCount, int depaturesCountAout) {
        this.tasksProcessed = tasksProcessed;
        this.busyTime = busyTime;
        this.totalTime = totalTime;
        this.totalDelay = totalDelay;
        this.TotalCountQueue = TotalCountQueue;
        this.TotalCountQueueInLn = TotalCountQueueInLn;
        this.depaturesCount = depaturesCount;
        this.depaturesCountAout = depaturesCountAout;
    }
}

