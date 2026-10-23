package org.example.analysis;

import org.example.model.ParsedResult;

import java.util.ArrayList;
import java.util.List;

// Каждая стратегия выполняет один расчёт.
public class KafkaAnalyzer {
    private final List<AnalysisStrategy> strategies = new ArrayList<>();

    public KafkaAnalyzer() {
        strategies.add(new CountByTopicStrategy());
        strategies.add(new CountByPartitionStrategy());
        strategies.add(new CountByEventTypeStrategy());
        strategies.add(new MaxOffsetByTopicStrategy());
        strategies.add(new MaxOffsetByPartitionStrategy());
        strategies.add(new CountPaymentEventsStrategy());
    }

    public KafkaAnalysisReport analyze(ParsedResult parsedResult) {
        AnalysisAccumulator result = new AnalysisAccumulator();
        for (AnalysisStrategy strategy : strategies) {
            strategy.analyze(parsedResult.getRecords(), result);
        }
        return new KafkaAnalysisReport(parsedResult, result);
    }
}
