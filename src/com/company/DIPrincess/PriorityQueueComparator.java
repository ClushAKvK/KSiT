package com.company.DIPrincess;

import java.util.Comparator;

public class PriorityQueueComparator implements Comparator<Event> {

    @Override
    public int compare(Event e1, Event e2) {
        return Double.compare(e1.task.serviceTime, e2.task.serviceTime);
    }
}
