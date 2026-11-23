package com.tradestream.consumer.listener;

import com.tradestream.consumer.entity.PositionEntity;
import com.tradestream.consumer.model.TradeEvent;
import com.tradestream.consumer.service.PositionAggregator;
import com.tradestream.consumer.websocket.TradeStreamBroadcaster;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TradeEventListener {

    private final PositionAggregator aggregator;
    private final TradeStreamBroadcaster broadcaster;
    private final Counter tradeCounter;
    private final Counter errorCounter;

    public TradeEventListener(PositionAggregator aggregator,
                               TradeStreamBroadcaster broadcaster,
                               MeterRegistry registry) {
        this.aggregator = aggregator;
        this.broadcaster = broadcaster;
        this.tradeCounter = Counter.builder("tradestream.trades.processed")
                .description("Total trade events processed")
                .register(registry);
        this.errorCounter = Counter.builder("tradestream.trades.errors")
                .description("Total trade processing errors")
                .register(registry);
    }

    @KafkaListener(
            topics = "${kafka.topics.trade-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3"
    )
    public void onTradeEvent(
            @Payload TradeEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received trade: eventId={} trader={} symbol={} side={} qty={} price={} [partition={} offset={}]",
                event.getEventId(), event.getTraderId(), event.getSymbol(),
                event.getSide(), event.getQuantity(), event.getPrice(),
                partition, offset);

        try {
            PositionEntity updated = aggregator.process(event);
            tradeCounter.increment();

            if (updated != null) {
                broadcaster.broadcastTrade(event);
                broadcaster.broadcastPosition(updated);
            }
        } catch (Exception ex) {
            errorCounter.increment();
            log.error("Error processing event {}: {}", event.getEventId(), ex.getMessage(), ex);
            // Don't rethrow — let Kafka commit the offset so we don't block the partition
            // Dead-letter handling can be added in a follow-up
        }
    }
}
