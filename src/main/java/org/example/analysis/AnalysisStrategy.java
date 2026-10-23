package org.example.analysis;

import org.example.model.KafkaEvent;

import java.util.List;

/** Одна независимая операция подсчёта статистики. */
public interface AnalysisStrategy {
    void analyze(List<KafkaEvent> records, AnalysisAccumulator result);
}
