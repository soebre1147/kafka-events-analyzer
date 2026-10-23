package org.example.storage;

import org.example.analysis.KafkaAnalysisReport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Сохраняет и восстанавливает уже рассчитанный отчёт через Java Serialization. */
public final class JavaReportSerializer {
    public void save(Path file, KafkaAnalysisReport report) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(file))) {
            output.writeObject(report);
        }
    }

    public KafkaAnalysisReport load(Path file) throws IOException {
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(file))) {
            Object restored = input.readObject();
            if (!(restored instanceof KafkaAnalysisReport)) {
                throw new IOException("файл не содержит KafkaAnalysisReport");
            }
            return (KafkaAnalysisReport) restored;
        } catch (ClassNotFoundException exception) {
            throw new IOException("не удалось восстановить класс отчёта", exception);
        }
    }
}
