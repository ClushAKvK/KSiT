package com.company.ClushA;

public class SimulationResult {
    public int tasksProcessed;
    public double busyTime;
    public double totalTime;
    public double totalDelay;
    public double[] waitTimeCDF;
    public double rejectionProbability;

    public SimulationResult(int tasksProcessed, double busyTime, double totalTime, double totalDelay,
                            double[] waitTimeCDF, int denials, int totalArrivals) {
        this.tasksProcessed = tasksProcessed;
        this.busyTime = busyTime;
        this.totalTime = totalTime;
        this.totalDelay = totalDelay;
        this.waitTimeCDF = waitTimeCDF;
        this.rejectionProbability = (double) denials / totalArrivals;
    }

    public double getWaitTimeProbability(double x) {
        if (waitTimeCDF == null || waitTimeCDF.length == 0) return 0;
        double maxWait = waitTimeCDF.length * x / 100;
        int index = (int)(x * waitTimeCDF.length / maxWait);
        index = Math.min(index, waitTimeCDF.length - 1);
        return waitTimeCDF[index];
    }
}

