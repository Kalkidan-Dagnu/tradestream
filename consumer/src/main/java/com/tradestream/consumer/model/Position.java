package com.tradestream.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private String traderId;
    private String symbol;
    private BigDecimal netQuantity;      // positive = long, negative = short
    private BigDecimal averagePrice;
    private BigDecimal realizedPnl;
    private BigDecimal unrealizedPnl;
    private Instant lastUpdated;
}
