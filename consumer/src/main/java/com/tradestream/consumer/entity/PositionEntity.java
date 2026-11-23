package com.tradestream.consumer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "positions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"trader_id", "symbol"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trader_id", nullable = false, length = 64)
    private String traderId;

    @Column(nullable = false, length = 16)
    private String symbol;

    @Column(name = "net_quantity", nullable = false, precision = 20, scale = 8)
    @Builder.Default
    private BigDecimal netQuantity = BigDecimal.ZERO;

    @Column(name = "average_price", nullable = false, precision = 20, scale = 8)
    @Builder.Default
    private BigDecimal averagePrice = BigDecimal.ZERO;

    @Column(name = "realized_pnl", nullable = false, precision = 20, scale = 8)
    @Builder.Default
    private BigDecimal realizedPnl = BigDecimal.ZERO;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @PrePersist
    @PreUpdate
    void touch() {
        lastUpdated = Instant.now();
    }
}
