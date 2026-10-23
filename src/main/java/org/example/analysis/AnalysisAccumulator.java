package org.example.analysis;

import org.example.model.EventType;

import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

// Здесь хранятся счётчики, которые заполняют стратегии.
public class AnalysisAccumulator {
    private final Map<String, Long> eventsByTopic = new TreeMap<>();
    private final Map<Integer, Long> eventsByPartition = new TreeMap<>();
    private final Map<EventType, Long> eventsByType = new EnumMap<>(EventType.class);
    private final Map<String, Long> maxOffsetByTopic = new TreeMap<>();
    private final Map<Integer, Long> maxOffsetByPartition = new TreeMap<>();
    private long paymentEvents;

    public AnalysisAccumulator() {
        for (EventType type : EventType.values()) {
            eventsByType.put(type, 0L);
        }
    }

    public void countTopic(String topic) {
        long count = 0;
        if (eventsByTopic.containsKey(topic)) {
            count = eventsByTopic.get(topic);
        }
        eventsByTopic.put(topic, count + 1);
    }

    public void countPartition(int partition) {
        long count = 0;
        if (eventsByPartition.containsKey(partition)) {
            count = eventsByPartition.get(partition);
        }
        eventsByPartition.put(partition, count + 1);
    }

    public void countType(EventType type) {
        long count = eventsByType.get(type);
        eventsByType.put(type, count + 1);
    }

    public void updateTopicOffset(String topic, long offset) {
        if (!maxOffsetByTopic.containsKey(topic) || offset > maxOffsetByTopic.get(topic)) {
            maxOffsetByTopic.put(topic, offset);
        }
    }

    public void updatePartitionOffset(int partition, long offset) {
        if (!maxOffsetByPartition.containsKey(partition)
                || offset > maxOffsetByPartition.get(partition)) {
            maxOffsetByPartition.put(partition, offset);
        }
    }

    public void countPaymentEvent() {
        paymentEvents++;
    }

    Map<String, Long> getEventsByTopic() {
        return eventsByTopic;
    }

    Map<Integer, Long> getEventsByPartition() {
        return eventsByPartition;
    }

    Map<EventType, Long> getEventsByType() {
        return eventsByType;
    }

    Map<String, Long> getMaxOffsetByTopic() {
        return maxOffsetByTopic;
    }

    Map<Integer, Long> getMaxOffsetByPartition() {
        return maxOffsetByPartition;
    }

    long getPaymentEvents() {
        return paymentEvents;
    }
}
