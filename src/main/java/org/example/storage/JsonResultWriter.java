package org.example.storage;

import org.example.model.InvalidLine;
import org.example.model.KafkaEvent;
import org.example.model.ParsedResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Записывает правильные события и ошибки в JSON-файл.
public class JsonResultWriter {
    public void write(Path file, ParsedResult parsed) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"variant\": ");
        json.append(toJsonString(parsed.getVariant()));
        json.append(",\n  \"totalLines\": ").append(parsed.getTotalLines());
        json.append(",\n  \"validLines\": ").append(parsed.getValidLines());
        json.append(",\n  \"invalidLines\": ").append(parsed.getInvalidLines());
        json.append(",\n  \"records\": [");

        List<KafkaEvent> records = parsed.getRecords();
        for (int index = 0; index < records.size(); index++) {
            KafkaEvent event = records.get(index);
            if (index > 0) {
                json.append(',');
            }
            json.append('\n');
            json.append("    {\"topic\": ");
            json.append(toJsonString(event.getTopic()));
            json.append(", \"partition\": ").append(event.getPartition());
            json.append(", \"offset\": ").append(event.getOffset());
            json.append(", \"eventType\": ");
            json.append(toJsonString(event.getEventType().name()));
            json.append('}');
        }
        if (!records.isEmpty()) {
            json.append('\n');
        }
        json.append("  ],\n  \"invalidRecords\": [");

        List<InvalidLine> invalidRecords = parsed.getInvalidRecords();
        for (int index = 0; index < invalidRecords.size(); index++) {
            InvalidLine invalid = invalidRecords.get(index);
            if (index > 0) {
                json.append(',');
            }
            json.append('\n');
            json.append("    {\"lineNumber\": ").append(invalid.getLineNumber());
            json.append(", \"line\": ");
            json.append(toJsonString(invalid.getLine()));
            json.append(", \"reason\": ");
            json.append(toJsonString(invalid.getReason()));
            json.append('}');
        }
        if (!invalidRecords.isEmpty()) {
            json.append('\n');
        }
        json.append("  ]\n}\n");

        Files.writeString(file, json, StandardCharsets.UTF_8);
    }

    // Кавычки, обратный слеш и управляющие символы надо экранировать в JSON.
    private String toJsonString(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                    break;
            }
        }
        return "\"" + escaped.toString() + "\"";
    }
}
