package com.company.DIPrincess;

import java.util.List;

public class SimulationResult {
    public int tasksProcessed;
    public double busyTime;
    public double totalTime;
    public double totalDelay;
    public int TotalCountQueue;
    public int TotalCountQueueInLn;
    public int depaturesCount;
    public int depaturesCountAout;
    public List<Integer> queueSizes;  // Новый список для размеров очереди
    public List<Double> intervals;    // Новый список для длительностей интервалов
    public List<Double> delays; // <--- ДОБАВЛЕНО


    public SimulationResult(int tasksProcessed, double busyTime, double totalTime, double totalDelay,
                            int TotalCountQueue, int TotalCountQueueInLn, int depaturesCount, int depaturesCountAout,
                            List<Integer> queueSizes, List<Double> intervals, List<Double> delays)  {
        this.tasksProcessed = tasksProcessed;
        this.busyTime = busyTime;
        this.totalTime = totalTime;
        this.totalDelay = totalDelay;
        this.TotalCountQueue = TotalCountQueue;
        this.TotalCountQueueInLn = TotalCountQueueInLn;
        this.depaturesCount = depaturesCount;
        this.depaturesCountAout = depaturesCountAout;
        this.queueSizes = queueSizes;
        this.intervals = intervals;
        this.delays = delays;
    }
}

