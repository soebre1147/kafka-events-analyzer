package org.example.report;

import org.example.analysis.KafkaAnalysisReport;
import org.example.model.EventType;
import org.example.model.InvalidLine;

import java.util.Map;

// Собирает текст отчёта. Подсчёты уже сделаны в анализаторе.
public class KafkaReportBuilder {
    public String build(KafkaAnalysisReport report) {
        StringBuilder result = new StringBuilder();
        result.append("============================================================\n");
        result.append("                 ОТЧЁТ ПО KAFKA-LIKE СОБЫТИЯМ\n");
        result.append("============================================================\n");
        result.append("Вариант: ").append(report.getVariant()).append("\n\n");

        section(result, "1. Общая информация");
        result.append("Всего строк обработано: ").append(report.getTotalLines()).append('\n');
        result.append("Корректных записей:     ").append(report.getValidLines()).append('\n');
        result.append("Проблемных строк:       ").append(report.getInvalidLines()).append("\n\n");

        section(result, "2. События по topic");
        if (report.getEventsByTopic().isEmpty()) {
            result.append("Нет данных.\n");
        }
        for (Map.Entry<String, Long> entry : report.getEventsByTopic().entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }
        result.append('\n');

        section(result, "3. События по partition");
        if (report.getEventsByPartition().isEmpty()) {
            result.append("Нет данных.\n");
        }
        for (Map.Entry<Integer, Long> entry : report.getEventsByPartition().entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }
        result.append('\n');

        section(result, "4. События по eventType");
        for (Map.Entry<EventType, Long> entry : report.getEventsByType().entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }
        result.append('\n');

        section(result, "5. Максимальный offset по topic");
        if (report.getMaxOffsetByTopic().isEmpty()) {
            result.append("Нет данных.\n");
        }
        for (Map.Entry<String, Long> entry : report.getMaxOffsetByTopic().entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }
        result.append('\n');

        section(result, "6. Максимальный offset по partition");
        if (report.getMaxOffsetByPartition().isEmpty()) {
            result.append("Нет данных.\n");
        }
        for (Map.Entry<Integer, Long> entry : report.getMaxOffsetByPartition().entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }
        result.append('\n');

        section(result, "7. События оплаты");
        result.append("PAYMENT_STARTED + PAYMENT_FAILED: ")
                .append(report.getPaymentEvents()).append("\n\n");

        section(result, "8. Проблемные строки");
        if (report.getInvalidRecords().isEmpty()) {
            result.append("Нет проблемных строк.\n");
        } else {
            for (InvalidLine invalid : report.getInvalidRecords()) {
                result.append("Строка ").append(invalid.getLineNumber())
                        .append(": ").append(invalid.getLine()).append('\n');
                result.append("Причина: ").append(invalid.getReason()).append("\n\n");
            }
        }
        result.append("============================================================\n");
        return result.toString();
    }

    private void section(StringBuilder result, String title) {
        result.append("------------------------------------------------------------\n");
        result.append(title).append('\n');
        result.append("------------------------------------------------------------\n");
    }
}
