package com.company.DIPrincess;

public class Event implements Comparable<Event> {
    public enum Type { ARRIVAL, DEPARTURE }
    public double time;
    public Type type;
    public Task task;

    public Event(double time, Type type, Task task) {
        this.time = time;
        this.type = type;
        this.task = task;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.time, other.time);
    }
}

