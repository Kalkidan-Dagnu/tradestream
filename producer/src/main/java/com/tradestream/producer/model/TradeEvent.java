package com.tradestream.producer.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeEvent {

    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    @NotBlank
    private String traderId;

    @NotNull
    private Symbol symbol;

    @NotNull
    private OrderSide side;

    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    /** Notional value = quantity × price */
    public BigDecimal notional() {
        return quantity.multiply(price);
    }

    @Builder.Default
    private Instant timestamp = Instant.now();

    private String exchange;
}
