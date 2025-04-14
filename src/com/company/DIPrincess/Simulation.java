package com.company.DIPrincess;

import java.util.PriorityQueue;

public class Simulation {
    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>(new PriorityQueueComparator());
    private final Server server;
    private final double maxTime;
    private final double meanTau;
    private final double meanSigma;
    private final double R_a;
    private final double R_b;
    private final double stddev;
    private final int Ln;
    private final double Aout;

    private int TotalCountQueue = 0;
    private int TotalCountQueueInLn = 0;

    private int depaturesCount = 0;
    private int depaturesCountAout = 0;
    private double lastDepartureTime = 0;

    private double globalClock = 0.0;
    private int taskCounter = 0;

    public Simulation(int bufferSize, double maxTime, double meanTau, double meanSigma, double R_a, double R_b, int Ln, double Aout) {
        this.server = new Server(bufferSize);
        this.maxTime = maxTime;
        this.meanTau = meanTau;
        this.meanSigma = meanSigma;
        this.R_a = R_a;
        this.R_b = R_b;
        this.Ln = Ln;
        this.Aout = Aout;

        this.stddev = meanSigma / 3.0;

        if (meanSigma / meanTau >= 1.0) {
            throw new IllegalArgumentException("Система нестабильна: ρ >= 1");
        }
    }

    public void run() {
        scheduleNextArrival(0);

        while (globalClock < maxTime && !eventQueue.isEmpty()) {
            // System.out.println(eventQueue.size());
            TotalCountQueue++;
            if (eventQueue.size() == Ln ) TotalCountQueueInLn++;
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
            task.departureTime = globalClock + task.serviceTime;
            scheduleEvent(task.departureTime, Event.Type.DEPARTURE, task);
        } else {
            server.addToBuffer(task);
        }

        if (globalClock < maxTime) {
            scheduleNextArrival(globalClock);
        }
    }


    private void draft_handleArrival(Event e) {
        Task task = e.task;

        if (!server.isBusy()) {
            server.startTask(task, globalClock);
            task.departureTime = globalClock + task.serviceTime;
            scheduleEvent(task.departureTime, Event.Type.DEPARTURE, task);
        } else {
            server.addToBuffer(task);
        }

        // Планируем следующее прибытие
        if (globalClock < maxTime) {
            scheduleNextArrival(globalClock);
        }
    }

    private void draft_handleDeparture(Event e) {
//        server.finishCurrentTask();
        server.processed++;

        // metrick task 2
        depaturesCount++;
        if (e.task.departureTime - lastDepartureTime < Aout)
            depaturesCountAout++;
        lastDepartureTime = e.task.departureTime;

        Task nextTask = server.fetchFromBuffer();
        if (nextTask != null) {
            server.startTask(nextTask, globalClock);
            nextTask.departureTime = globalClock + nextTask.serviceTime;
            scheduleEvent(nextTask.departureTime, Event.Type.DEPARTURE, nextTask);
        }
    }

    private void handleDeparture(Event e) {
        Task completedTask = e.task;

        server.finishTask(completedTask);
        server.processed++;

        // Метрика Aout
        depaturesCount++;
        if (completedTask.departureTime - lastDepartureTime < Aout) {
            depaturesCountAout++;
        }
        lastDepartureTime = completedTask.departureTime;

        // Проверяем буфер: можем запустить новую задачу, если место освободилось
        while (!server.isBusy()) {
            Task nextTask = server.fetchFromBuffer();
            if (nextTask == null) break;

            server.startTask(nextTask, globalClock);
            nextTask.departureTime = globalClock + nextTask.serviceTime;
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
    private double draft_sampleTau() {
        return -meanTau * Math.log(1 - Math.random());
    }

    // Равномерно распеределение на участке R[a, b]
    private double sampleTau() {
        return R_a + (R_b - R_a) * Math.random();
    }

    // Распределение sigma: можно заменить на любое другое
    private double draft_sampleSigma() {
        return Math.max(0.0, new java.util.Random().nextGaussian() * (meanSigma / 3) + meanSigma);
    }

    // Гаусовскокое распеределение
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

        while (globalClock < maxTime && !eventQueue.isEmpty()) {
            TotalCountQueue++;
            if (eventQueue.size() == Ln) {
                TotalCountQueueInLn++;
            }

            Event event = eventQueue.poll();
            globalClock = event.time;

            switch (event.type) {
                case ARRIVAL : handleArrival(event);
                case DEPARTURE : {
                    totalDelay += globalClock - event.task.arrivalTime;
                    handleDeparture(event);
                }
            }
        }

        return new SimulationResult(server.processed, server.busyTime, globalClock, totalDelay,
                TotalCountQueue, TotalCountQueueInLn, depaturesCount, depaturesCountAout);
    }
}

