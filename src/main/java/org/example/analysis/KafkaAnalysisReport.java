package org.example.analysis;

import org.example.model.EventType;
import org.example.model.InvalidLine;
import org.example.model.ParsedResult;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Неизменяемый сериализуемый отчёт с уже рассчитанной статистикой. */
public final class KafkaAnalysisReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String variant;
    private final long totalLines;
    private final long validLines;
    private final long invalidLines;
    private final Map<String, Long> eventsByTopic;
    private final Map<Integer, Long> eventsByPartition;
    private final Map<EventType, Long> eventsByType;
    private final Map<String, Long> maxOffsetByTopic;
    private final Map<Integer, Long> maxOffsetByPartition;
    private final long paymentEvents;
    private final List<InvalidLine> invalidRecords;

    KafkaAnalysisReport(ParsedResult parsed, AnalysisAccumulator calculated) {
        this.variant = parsed.getVariant();
        this.totalLines = parsed.getTotalLines();
        this.validLines = parsed.getValidLines();
        this.invalidLines = parsed.getInvalidLines();
        this.eventsByTopic = Collections.unmodifiableMap(new TreeMap<>(calculated.getEventsByTopic()));
        this.eventsByPartition = Collections.unmodifiableMap(new TreeMap<>(calculated.getEventsByPartition()));
        this.eventsByType = Collections.unmodifiableMap(new EnumMap<>(calculated.getEventsByType()));
        this.maxOffsetByTopic = Collections.unmodifiableMap(new TreeMap<>(calculated.getMaxOffsetByTopic()));
        this.maxOffsetByPartition = Collections.unmodifiableMap(new TreeMap<>(calculated.getMaxOffsetByPartition()));
        this.paymentEvents = calculated.getPaymentEvents();
        this.invalidRecords = List.copyOf(parsed.getInvalidRecords());
    }

    public String getVariant() {
        return variant;
    }

    public long getTotalLines() {
        return totalLines;
    }

    public long getValidLines() {
        return validLines;
    }

    public long getInvalidLines() {
        return invalidLines;
    }

    public Map<String, Long> getEventsByTopic() {
        return eventsByTopic;
    }

    public Map<Integer, Long> getEventsByPartition() {
        return eventsByPartition;
    }

    public Map<EventType, Long> getEventsByType() {
        return eventsByType;
    }

    public Map<String, Long> getMaxOffsetByTopic() {
        return maxOffsetByTopic;
    }

    public Map<Integer, Long> getMaxOffsetByPartition() {
        return maxOffsetByPartition;
    }

    public long getPaymentEvents() {
        return paymentEvents;
    }

    public List<InvalidLine> getInvalidRecords() {
        return invalidRecords;
    }
}
