package org.example.model;

import java.util.Objects;

/** Неизменяемая модель одной корректной записи. */
public final class KafkaEvent {
    private final String topic;
    private final int partition;
    private final long offset;
    private final EventType eventType;

    public KafkaEvent(String topic, int partition, long offset, EventType eventType) {
        this.topic = Objects.requireNonNull(topic, "topic");
        this.partition = partition;
        this.offset = offset;
        this.eventType = Objects.requireNonNull(eventType, "eventType");
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public EventType getEventType() {
        return eventType;
    }
}
