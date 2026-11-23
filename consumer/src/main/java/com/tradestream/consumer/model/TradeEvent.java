package com.tradestream.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeEvent {
    private String eventId;
    private String traderId;
    private String symbol;
    private String side;
    private BigDecimal quantity;
    private BigDecimal price;
    private Instant timestamp;
    private String exchange;

    public BigDecimal notional() {
        return quantity.multiply(price);
    }
}
