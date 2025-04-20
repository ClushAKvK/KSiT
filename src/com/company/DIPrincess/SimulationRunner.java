package com.company.DIPrincess;

import java.util.ArrayList;
import java.util.List;

public class SimulationRunner {
    public static void main(String[] args) {
        final int experiments = 10000;
        final int bufferSize = 15;
        final double timePerExperiment = 10.0;

        final double meanTau = 1.6;
        final double meanSigma = 1.5;

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

        List<Integer> queueSizes = new ArrayList<>();
        List<Double> intervals = new ArrayList<>();
        List<Double> delays = new ArrayList<>();

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

            queueSizes.addAll(result.queueSizes);  // Добавляем данные о размерах очереди
            intervals.addAll(result.intervals);    // Добавляем данные о длительностях интервалов
            delays.addAll(result.delays);          // Время задержки задач в системе
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

        // Подсчитываем вероятности для L(n) и Aout(x)
        double[] P_Ln_1 = calculateProbabilityL_n(queueSizes);
        double[] P_Aout_1 = calculateProbabilityAout(intervals);
        double[] A_x = calculateA_x(intervals);
        double[] W_x = calculateW_x(delays);

        // Строим графики
        GraphBuilder.plotL_n(P_Ln_1);
        GraphBuilder.plotAout(P_Aout_1);
        GraphBuilder.plotA_x(A_x);
        GraphBuilder.plotW_x(W_x);
    }

    // Функция для подсчета вероятности L(n)
    public static double[] calculateProbabilityL_n(List<Integer> queueSizes) {
        int maxQueueSize = 10;  // Пример, можно настроить под нужды
        int[] counts = new int[maxQueueSize + 1];

        for (int size : queueSizes) {
            counts[size]++;
        }

        double[] probabilities = new double[maxQueueSize + 1];
        for (int i = 0; i <= maxQueueSize; i++) {
            probabilities[i] = (double) counts[i] / queueSizes.size();
        }

        return probabilities;
    }

    // Функция для подсчета вероятности Aout(x)
    public static double[] calculateProbabilityAout(List<Double> intervals) {
        int binCount = 10;  // Пример, можно настроить под нужды
        double maxInterval = intervals.stream().max(Double::compare).orElse(0.0);
        double minInterval = intervals.stream().min(Double::compare).orElse(0.0);
        double[] bins = new double[binCount];
        double binWidth = (maxInterval - minInterval) / binCount;

        for (double interval : intervals) {
            int binIndex = (int) ((interval - minInterval) / binWidth);
            bins[Math.min(binIndex, binCount - 1)]++;
        }

        for (int i = 0; i < binCount; i++) {
            bins[i] /= intervals.size();
        }

        return bins;
    }

    // Функция для подсчета A(x) - Функция распределения интервалов между событиями
    public static double[] calculateA_x(List<Double> intervals) {
        int binCount = 10;
        double maxInterval = intervals.stream().max(Double::compare).orElse(0.0);
        double minInterval = intervals.stream().min(Double::compare).orElse(0.0);
        double[] bins = new double[binCount];
        double binWidth = (maxInterval - minInterval) / binCount;

        for (double interval : intervals) {
            int binIndex = (int) ((interval - minInterval) / binWidth);
            bins[Math.min(binIndex, binCount - 1)]++;
        }

        for (int i = 0; i < binCount; i++) {
            bins[i] /= intervals.size();
        }

        return bins;
    }

    // Функция для подсчета W(x) - Функция распределения времени пребывания в системе
    public static double[] calculateW_x(List<Double> delays) {
        int binCount = 10;
        double maxDelay = delays.stream().max(Double::compare).orElse(0.0);
        double minDelay = delays.stream().min(Double::compare).orElse(0.0);
        double[] bins = new double[binCount];
        double binWidth = (maxDelay - minDelay) / binCount;

        for (double delay : delays) {
            int binIndex = (int) ((delay - minDelay) / binWidth);
            bins[Math.min(binIndex, binCount - 1)]++;
        }

        for (int i = 0; i < binCount; i++) {
            bins[i] /= delays.size();
        }

        return bins;
    }
}


