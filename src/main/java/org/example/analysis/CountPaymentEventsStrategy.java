package org.example.analysis;

import org.example.model.EventType;
import org.example.model.KafkaEvent;

import java.util.List;

public final class CountPaymentEventsStrategy implements AnalysisStrategy {
    @Override
    public void analyze(List<KafkaEvent> records, AnalysisAccumulator result) {
        for (KafkaEvent record : records) {
            EventType type = record.getEventType();
            if (type == EventType.PAYMENT_STARTED || type == EventType.PAYMENT_FAILED) {
                result.countPaymentEvent();
            }
        }
    }
}
