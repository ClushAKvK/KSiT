package com.company.Template;

import java.util.PriorityQueue;

public class Simulation {
    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();
    private final Server server;
    private final int maxTasks;
    private final double meanTau;
    private final double meanSigma;

    private double globalClock = 0.0;
    private int taskCounter = 0;

    public Simulation(int bufferSize, int maxTasks, double meanTau, double meanSigma) {
        this.server = new Server(bufferSize);
        this.maxTasks = maxTasks;
        this.meanTau = meanTau;
        this.meanSigma = meanSigma;

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
            task.departureTime = globalClock + task.serviceTime;
            scheduleEvent(task.departureTime, Event.Type.DEPARTURE, task);
        } else {
            server.addToBuffer(task);
        }

        // Планируем следующее прибытие
        if (taskCounter < maxTasks) {
            scheduleNextArrival(globalClock);
        }
    }

    private void handleDeparture(Event e) {
        server.finishCurrentTask();
        server.processed++;

        Task nextTask = server.fetchFromBuffer();
        if (nextTask != null) {
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
    private double sampleTau() {
        return -meanTau * Math.log(1 - Math.random());
    }

    // Распределение sigma: можно заменить на любое другое
    private double sampleSigma() {
        return Math.max(0.0, new java.util.Random().nextGaussian() * (meanSigma / 3) + meanSigma);
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

        while (server.processed < maxTasks && !eventQueue.isEmpty()) {
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

        return new SimulationResult(server.processed, server.busyTime, globalClock, totalDelay);
    }
}

