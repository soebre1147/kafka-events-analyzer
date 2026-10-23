package org.example.analysis;

import org.example.model.KafkaEvent;

import java.util.List;

public final class CountByTopicStrategy implements AnalysisStrategy {
    @Override
    public void analyze(List<KafkaEvent> records, AnalysisAccumulator result) {
        for (KafkaEvent record : records) {
            result.countTopic(record.getTopic());
        }
    }
}
