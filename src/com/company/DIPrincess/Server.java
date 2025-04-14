package com.company.DIPrincess;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Server {
    private final int bufferSize;
    private final Queue<Task> buffer;
    public List<Task> activeTasks = new ArrayList<>();
    public int processed = 0;
    public int denials = 0;
    public double busyTime = 0.0;

    public Server(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new LinkedList<>();
    }

    public boolean isBusy() {
        return activeTasks.size() >= 2;
    }

    public void startTask(Task task, double currentTime) {
        task.arrivalTime = currentTime;
        activeTasks.add(task);
    }

    public void finishTask(Task task) {
        activeTasks.remove(task);
    }

    public void addToBuffer(Task task) {
        if (buffer.size() < bufferSize) {
            buffer.add(task);
        } else {
            denials++;
        }
    }

    public Task fetchFromBuffer() {
        return buffer.poll();
    }
}


