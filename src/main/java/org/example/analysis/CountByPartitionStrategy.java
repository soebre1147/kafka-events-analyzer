package org.example.analysis;

import org.example.model.KafkaEvent;

import java.util.List;

public final class CountByPartitionStrategy implements AnalysisStrategy {
    @Override
    public void analyze(List<KafkaEvent> records, AnalysisAccumulator result) {
        for (KafkaEvent record : records) {
            result.countPartition(record.getPartition());
        }
    }
}
