package org.example.parser;

import org.example.model.EventType;
import org.example.model.KafkaEvent;

/** Разбирает ровно одну строку; не читает поток и не считает статистику. */
public final class EventParser {
    public KafkaEvent parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new ParseException("пустая строка");
        }

        String[] fields = line.trim().split("\\s+");
        if (fields.length != 4) {
            throw new ParseException("ожидалось 4 поля, получено " + fields.length);
        }

        int partition = parsePartition(fields[1]);
        long offset = parseOffset(fields[2]);
        EventType eventType;
        try {
            eventType = EventType.valueOf(fields[3]);
        } catch (IllegalArgumentException exception) {
            throw new ParseException("неизвестный eventType: " + fields[3]);
        }
        return new KafkaEvent(fields[0], partition, offset, eventType);
    }

    private int parsePartition(String value) {
        try {
            int partition = Integer.parseInt(value);
            if (partition < 0) {
                throw new ParseException("partition не может быть отрицательным: " + value);
            }
            return partition;
        } catch (NumberFormatException exception) {
            throw new ParseException("partition должен быть целым числом: " + value);
        }
    }

    private long parseOffset(String value) {
        try {
            long offset = Long.parseLong(value);
            if (offset < 0) {
                throw new ParseException("offset не может быть отрицательным: " + value);
            }
            return offset;
        } catch (NumberFormatException exception) {
            throw new ParseException("offset должен быть целым числом: " + value);
        }
    }
}
