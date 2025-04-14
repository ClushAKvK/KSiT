package com.company.DIPrincess;

public class SimulationRunner {
    public static void main(String[] args) {
        final int experiments = 10000;
        final int bufferSize = 15;
        final double timePerExperiment = 10.0;

        final double meanTau = 2.0;
        final double meanSigma = 1.0;

        final double R_a = 0.1;
        final double R_b = 5.2;

        final int Ln = 3;

        final double Aout = 0.3;

        double totalBusyTime = 0.0;
        double totalTime = 0.0;
        double totalDelay = 0.0;
        int totalTasks = 0;
        int TotalCountQueue = 0;
        int TotalCountQueueInLn = 0;
        int depaturesCount = 0;
        int depaturesCountAout = 0;

        for (int i = 0; i < experiments; i++) {
            Simulation sim = new Simulation(bufferSize, timePerExperiment, meanTau, meanSigma, R_a, R_b, Ln, Aout);
            SimulationResult result = sim.runAndCollect();

            totalBusyTime += result.busyTime;
            totalTime += result.totalTime;
            totalDelay += result.totalDelay;
            totalTasks += result.tasksProcessed;
            TotalCountQueue += result.TotalCountQueue;
            TotalCountQueueInLn += result.TotalCountQueueInLn;
            depaturesCount += result.depaturesCount;
            depaturesCountAout += result.depaturesCountAout;
        }

        double avgW = totalDelay / totalTasks;
        double avgPBusy = totalBusyTime / totalTime;
        double P_Ln = (double) TotalCountQueueInLn / TotalCountQueue;
        double P_Aout = (double) depaturesCountAout / depaturesCount;
        //System.out.println(depaturesCountAout+ " " + depaturesCount);

        System.out.printf("После %d экспериментов:\n", experiments);
        //System.out.printf("W(x) — Среднее время пребывания: %.4f\n", avgW);
        //System.out.printf("P_busy(x) — Доля занятости сервера: %.4f\n", avgPBusy);
        System.out.printf("L(n) — распределение вероятностей длины очереди, т.е. L(n) = P{длина очереди =n} %.4f\n", P_Ln);
        System.out.printf("A_out(x) — функция распределения вероятностей длительностей интервалов %.4f\n", P_Aout);
    }
}

