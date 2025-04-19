package com.company.ClushA;

public class SimulationRunner {
    public static void main(String[] args) {
        final int experiments = 10000;
        final int bufferSize = 8;
        final int tasksPerExperiment = 500;

        final double meanTau = 2.0;
        final double meanSigma = 1.5;

        double totalBusyTime = 0.0;
        double totalTime = 0.0;
        double totalDelay = 0.0;
        int totalTasks = 0;

        // Для анализа W(x)
        double targetX = 5.0; // Пример: хотим узнать P(w ≤ 5.0)
        double totalWx = 0;

        double totalRejectionProb = 0;

        for (int i = 0; i < experiments; i++) {
            Simulation sim = new Simulation(bufferSize, tasksPerExperiment, meanTau, meanSigma);
            SimulationResult result = sim.runAndCollect();

            totalBusyTime += result.busyTime;
            totalTime += result.totalTime;
            totalDelay += result.totalDelay;
            totalTasks += result.tasksProcessed;
        }

        double avgW = totalDelay / totalTasks;
        double avgPBusy = totalBusyTime / totalTime;

        System.out.printf("После %d экспериментов:\n", experiments);
        System.out.printf("W(x) — Среднее время пребывания: %.4f\n", avgW);
        System.out.printf("P_busy(x) — Доля занятости сервера: %.4f\n", avgPBusy);
    }
}

