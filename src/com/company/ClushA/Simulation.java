package com.company.ClushA;

import java.util.*;

public class Simulation {
    //private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();
    private final Queue<Event> eventQueue = new LinkedList<>();
    private final Server server;
    private final int maxTasks;
    private final double meanTau;
    private final double meanSigma;
    private final double stddev;

    // ..
    private List<Double> waitTimes = new ArrayList<>();

    private double globalClock = 0.0;
    private int taskCounter = 0;

    public Simulation(int bufferSize, int maxTasks, double meanTau, double meanSigma) {
        this.server = new Server(bufferSize);
        this.maxTasks = maxTasks;
        this.meanTau = meanTau;
        this.meanSigma = meanSigma;

        this.stddev = meanSigma / 3.0;

        if (meanSigma / meanTau >= 1.0) {
            throw new IllegalArgumentException("Система нестабильна: ρ >= 1");
        }
    }

    public void run() {
        scheduleNextArrival(0);

        while (globalClock < maxTasks && !eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            globalClock = event.time;

            switch (event.type) {
                case ARRIVAL : handleArrival(event);
                case DEPARTURE : handleDeparture(event);
            }
        }

        report();
    }

    private void handleArrival(Event e) {
        Task task = e.task;
        if (!server.isBusy()) {
            server.startTask(task, globalClock);

            task.queueEnterTime = globalClock;
            task.queueExitTime = globalClock;
            // Ограничиваем время обработки квантом
            double remainingTime = task.serviceTime;
            if (remainingTime > meanSigma) {
                task.serviceTime = meanSigma;  // Обрабатываем только quantum времени
                scheduleEvent(globalClock + meanSigma, Event.Type.DEPARTURE, task);
            } else {
                task.departureTime = globalClock + remainingTime;
                scheduleEvent(task.departureTime, Event.Type.DEPARTURE, task);
            }
        } else {
            task.queueEnterTime = globalClock; // Фиксируем вход в очередь
            server.addToBuffer(task);
        }

        if (taskCounter < maxTasks) {
            scheduleNextArrival(globalClock);
        }
    }

    private void handleDeparture(Event e) {
        Task currentTask = e.task;
        server.finishCurrentTask();
        server.processed++;

        // Если задача не завершена, возвращаем её в очередь
        double remainingTime = currentTask.serviceTime - meanSigma;
        if (remainingTime > 0) {
            currentTask.serviceTime = remainingTime;
            server.addToBuffer(currentTask);  // Возвращаем в очередь
        }

        // Берём следующую задачу из очереди
        Task nextTask = server.fetchFromBuffer();
        if (nextTask != null) {
            nextTask.queueExitTime = globalClock; // Фиксируем выход из очереди
            waitTimes.add(nextTask.getQueueWaitTime());
            server.startTask(nextTask, globalClock);
            double processingTime = Math.min(nextTask.serviceTime, meanSigma);
            nextTask.departureTime = globalClock + processingTime;
            scheduleEvent(nextTask.departureTime, Event.Type.DEPARTURE, nextTask);
        }
    }

    private void scheduleNextArrival(double currentTime) {
        Task task = new Task();
        task.id = taskCounter++;
        task.arrivalTime = currentTime + sampleTau();
        task.serviceTime = sampleSigma();

        scheduleEvent(task.arrivalTime, Event.Type.ARRIVAL, task);
    }

    private void scheduleEvent(double time, Event.Type type, Task task) {
        eventQueue.add(new Event(time, type, task));
    }

    // Распределение tau: экспоненциальное (пуассоновский поток)
    private double sampleTau() {
        // Параметры для распределения Эрланга
        final int k = 5; // порядок распределения Эрланга
        double erlangSum = 0.0;

        // Суммируем k экспоненциальных случайных величин
        for (int i = 0; i < k; i++) {
            erlangSum += -meanTau * Math.log(1 - Math.random());
        }

        // Добавляем детерминированную составляющую (например, 10% от среднего)
        final double deterministicComponent = 0.1 * meanTau;

        return erlangSum / k + deterministicComponent;
    }

    // Распределение sigma: можно заменить на любое другое
    private double sampleSigma() {
        double sigma;
        do {
            sigma = meanSigma + stddev * new java.util.Random().nextGaussian();
            sigma = Math.abs(sigma);
        } while (sigma == 0);

        return sigma;
    }

    private void report() {
        System.out.println("Total tasks processed: " + server.processed);
        System.out.println("Total denials: " + server.denials);
        System.out.println("Denial probability: " + ((double) server.denials / taskCounter));
        System.out.println("Server busy time: " + server.busyTime);
        System.out.println("Total simulation time: " + globalClock);
    }

    public SimulationResult runAndCollect() {
        scheduleNextArrival(0);

        double totalDelay = 0;
        waitTimes.clear();

        while (server.processed < maxTasks && !eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            globalClock = event.time;

            switch (event.type) {
                case ARRIVAL : handleArrival(event); break;
                case DEPARTURE : {
                    totalDelay += globalClock - event.task.arrivalTime;
                    handleDeparture(event);
                    break;
                }
            }
        }

        //return new SimulationResult(server.processed, server.busyTime, globalClock, totalDelay);
        return new SimulationResult(
                server.processed,
                server.busyTime,
                globalClock,
                totalDelay,
                calculateWaitTimeCDF(),
                server.denials,
                server.totalArrivals// Добавляем CDF
        );
    }

    private double[] calculateWaitTimeCDF() {
        // Сортировка времен ожидания
        waitTimes.sort(Double::compare);

        // Создание CDF с 100 точками
        double[] cdf = new double[100];
        int n = waitTimes.size();

        if (n == 0) return cdf;

        double maxWait = waitTimes.get(n-1);
        for (int i = 0; i < 100; i++) {
            double threshold = maxWait * (i+1) / 100;
            int count = 0;
            for (double wt : waitTimes) {
                if (wt <= threshold) count++;
            }
            cdf[i] = (double) count / n;
        }
        return cdf;
    }
}

