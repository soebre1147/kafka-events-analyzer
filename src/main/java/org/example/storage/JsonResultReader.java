package org.example.storage;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.EventType;
import org.example.model.InvalidLine;
import org.example.model.KafkaEvent;
import org.example.model.ParsedResult;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Восстанавливает сохранённые события и ошибки из JSON.
public class JsonResultReader {
    public ParsedResult read(Path file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

        JsonNode root;
        try (Reader input = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            root = mapper.readTree(input);
        }
        if (root == null || !root.isObject()) {
            throw new IOException("JSON должен содержать объект результата анализа");
        }

        String variant = readString(root, "variant");
        if (!ParsedResult.VARIANT.equals(variant)) {
            throw new IOException("JSON относится к другому варианту: " + variant);
        }

        long totalLines = readNumber(root, "totalLines");
        long validLines = readNumber(root, "validLines");
        long invalidLines = readNumber(root, "invalidLines");
        List<KafkaEvent> records = new ArrayList<>();
        List<InvalidLine> invalidRecords = new ArrayList<>();

        for (JsonNode record : readArray(root, "records")) {
            String topic = readString(record, "topic");
            long partition = readNumber(record, "partition");
            long offset = readNumber(record, "offset");
            String typeName = readString(record, "eventType");

            if (topic.isBlank()) {
                throw new IOException("topic в JSON не должен быть пустым");
            }
            if (partition > Integer.MAX_VALUE) {
                throw new IOException("partition в JSON слишком большой");
            }

            EventType type;
            try {
                type = EventType.valueOf(typeName);
            } catch (IllegalArgumentException exception) {
                throw new IOException("неизвестный eventType в JSON: " + typeName);
            }
            records.add(new KafkaEvent(topic, (int) partition, offset, type));
        }

        for (JsonNode invalid : readArray(root, "invalidRecords")) {
            long lineNumber = readNumber(invalid, "lineNumber");
            String line = readString(invalid, "line");
            String reason = readString(invalid, "reason");
            if (lineNumber == 0 || lineNumber > totalLines) {
                throw new IOException("неверный номер проблемной строки в JSON: " + lineNumber);
            }
            invalidRecords.add(new InvalidLine(lineNumber, line, reason));
        }

        if (validLines != records.size() || invalidLines != invalidRecords.size()
                || totalLines != (long) records.size() + invalidRecords.size()) {
            throw new IOException("счётчики в JSON не совпадают с количеством записей");
        }
        return new ParsedResult(totalLines, records, invalidRecords);
    }

    private String readString(JsonNode object, String field) throws IOException {
        JsonNode value = object.get(field);
        if (value == null || !value.isTextual()) {
            throw new IOException("поле " + field + " в JSON должно быть строкой");
        }
        return value.asText();
    }

    private long readNumber(JsonNode object, String field) throws IOException {
        JsonNode value = object.get(field);
        if (value == null || !value.isIntegralNumber() || !value.canConvertToLong()) {
            throw new IOException("поле " + field + " в JSON должно быть целым числом");
        }
        long number = value.asLong();
        if (number < 0) {
            throw new IOException("поле " + field + " в JSON не может быть отрицательным");
        }
        return number;
    }

    private JsonNode readArray(JsonNode object, String field) throws IOException {
        JsonNode value = object.get(field);
        if (value == null || !value.isArray()) {
            throw new IOException("поле " + field + " в JSON должно быть массивом");
        }
        return value;
    }
}
