package com.tradestream.consumer.websocket;

import com.tradestream.consumer.entity.PositionEntity;
import com.tradestream.consumer.model.TradeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeStreamBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    /** Broadcast a raw trade event to all subscribed dashboard clients. */
    public void broadcastTrade(TradeEvent event) {
        messagingTemplate.convertAndSend("/topic/trades", event);
        log.debug("Broadcast trade event {} to /topic/trades", event.getEventId());
    }

    /** Broadcast an updated position to all subscribed dashboard clients. */
    public void broadcastPosition(PositionEntity position) {
        messagingTemplate.convertAndSend("/topic/positions", position);
        log.debug("Broadcast position update trader={} symbol={} to /topic/positions",
                position.getTraderId(), position.getSymbol());
    }
}
