package com.company.ClushA;

import java.util.LinkedList;
import java.util.Queue;

public class Server {
    private final int bufferSize;
    private final Queue<Task> buffer = new LinkedList<>();
    private Task currentTask = null;

    public int denials = 0;
    public int processed = 0;
    public double busyTime = 0.0;

    public int totalArrivals = 0;

    public Server(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public boolean isBusy() {
        return currentTask != null;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void startTask(Task task, double currentTime) {
        currentTask = task;
        busyTime += task.serviceTime;
    }

    public Task finishCurrentTask() {
        Task finished = currentTask;
        currentTask = null;
        return finished;
    }

    public boolean addToBuffer(Task task) {
        totalArrivals++;
        if (buffer.size() < bufferSize) {
            buffer.add(task);
            return true;
        } else {
            denials++;
            return false;
        }
    }

    public Task fetchFromBuffer() {
        return buffer.poll();
    }
}

