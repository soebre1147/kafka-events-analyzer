package org.example.model;

import java.util.List;
import java.util.Objects;

/** Результат чтения входного потока до расчёта статистики. */
public final class ParsedResult {
    public static final String VARIANT = "Kafka-like события";

    private final long totalLines;
    private final List<KafkaEvent> records;
    private final List<InvalidLine> invalidRecords;

    public ParsedResult(long totalLines, List<KafkaEvent> records, List<InvalidLine> invalidRecords) {
        this.records = List.copyOf(Objects.requireNonNull(records, "records"));
        this.invalidRecords = List.copyOf(Objects.requireNonNull(invalidRecords, "invalidRecords"));
        if (totalLines != (long) this.records.size() + this.invalidRecords.size()) {
            throw new IllegalArgumentException("Число строк не совпадает с числом записей");
        }
        this.totalLines = totalLines;
    }

    public String getVariant() {
        return VARIANT;
    }

    public long getTotalLines() {
        return totalLines;
    }

    public long getValidLines() {
        return records.size();
    }

    public long getInvalidLines() {
        return invalidRecords.size();
    }

    public List<KafkaEvent> getRecords() {
        return records;
    }

    public List<InvalidLine> getInvalidRecords() {
        return invalidRecords;
    }
}
