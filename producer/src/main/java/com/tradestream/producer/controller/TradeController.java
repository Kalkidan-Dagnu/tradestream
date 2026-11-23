package com.tradestream.producer.controller;

import com.tradestream.producer.model.TradeEvent;
import com.tradestream.producer.service.TradeEventPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeEventPublisher publisher;

    /**
     * Publish a single trade event.
     * POST /api/trades
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> publishTrade(@Valid @RequestBody TradeEvent event) {
        publisher.publish(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "eventId", event.getEventId(),
                        "status", "accepted"
                ));
    }
}
