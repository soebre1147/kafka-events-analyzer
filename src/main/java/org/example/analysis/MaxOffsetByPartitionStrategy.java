package org.example.analysis;

import org.example.model.KafkaEvent;

import java.util.List;

public final class MaxOffsetByPartitionStrategy implements AnalysisStrategy {
    @Override
    public void analyze(List<KafkaEvent> records, AnalysisAccumulator result) {
        for (KafkaEvent record : records) {
            result.updatePartitionOffset(record.getPartition(), record.getOffset());
        }
    }
}
