package com.tradestream.producer.service;

import com.tradestream.producer.model.TradeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeEventPublisher {

    private final KafkaTemplate<String, TradeEvent> kafkaTemplate;

    @Value("${kafka.topics.trade-events}")
    private String topic;

    /**
     * Publish a TradeEvent to Kafka.
     * Key = traderId so all trades from the same trader go to the same partition,
     * preserving order per trader.
     */
    public CompletableFuture<SendResult<String, TradeEvent>> publish(TradeEvent event) {
        log.info("Publishing trade event: eventId={} trader={} symbol={} side={} qty={} price={}",
                event.getEventId(), event.getTraderId(), event.getSymbol(),
                event.getSide(), event.getQuantity(), event.getPrice());

        return kafkaTemplate.send(topic, event.getTraderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {}: {}", event.getEventId(), ex.getMessage());
                    } else {
                        log.debug("Event {} delivered to partition {} offset {}",
                                event.getEventId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
