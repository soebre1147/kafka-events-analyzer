package org.example.parser;

import org.example.model.InvalidLine;
import org.example.model.KafkaEvent;
import org.example.model.ParsedResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Читает строки и разделяет правильные записи и ошибки.
public class EventProcessor {
    private final EventParser parser = new EventParser();

    public ParsedResult read(InputStream input) throws IOException {
        List<KafkaEvent> records = new ArrayList<>();
        List<InvalidLine> invalidRecords = new ArrayList<>();
        long lineNumber = 0;

        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        String line = reader.readLine();
        while (line != null) {
            lineNumber++;
            try {
                KafkaEvent event = parser.parse(line);
                records.add(event);
            } catch (ParseException exception) {
                // Ошибка одной строки не мешает читать следующие строки.
                invalidRecords.add(new InvalidLine(lineNumber, line, exception.getMessage()));
            }
            line = reader.readLine();
        }

        return new ParsedResult(lineNumber, records, invalidRecords);
    }
}
