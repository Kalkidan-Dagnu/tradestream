package com.tradestream.consumer.service;

import com.tradestream.consumer.entity.PositionEntity;
import com.tradestream.consumer.entity.TradeEntity;
import com.tradestream.consumer.model.TradeEvent;
import com.tradestream.consumer.repository.PositionRepository;
import com.tradestream.consumer.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionAggregator {

    private final TradeRepository tradeRepository;
    private final PositionRepository positionRepository;

    /**
     * Process an incoming trade event:
     * 1. Idempotency check — skip if already processed
     * 2. Persist the trade record
     * 3. Upsert the position (average price, realized P&L)
     *
     * Returns the updated PositionEntity, or null if the event was a duplicate.
     */
    @Transactional
    public PositionEntity process(TradeEvent event) {
        // Idempotency guard
        if (tradeRepository.existsByEventId(event.getEventId())) {
            log.warn("Duplicate event {}, skipping", event.getEventId());
            return null;
        }

        // Persist trade
        TradeEntity trade = TradeEntity.builder()
                .eventId(event.getEventId())
                .traderId(event.getTraderId())
                .symbol(event.getSymbol())
                .side(event.getSide())
                .quantity(event.getQuantity())
                .price(event.getPrice())
                .notional(event.notional())
                .exchange(event.getExchange())
                .tradedAt(event.getTimestamp() != null ? event.getTimestamp() : Instant.now())
                .build();
        tradeRepository.save(trade);

        // Upsert position
        PositionEntity position = positionRepository
                .findByTraderIdAndSymbol(event.getTraderId(), event.getSymbol())
                .orElseGet(() -> PositionEntity.builder()
                        .traderId(event.getTraderId())
                        .symbol(event.getSymbol())
                        .build());

        boolean isBuy = "BUY".equalsIgnoreCase(event.getSide());
        BigDecimal qty = event.getQuantity();
        BigDecimal price = event.getPrice();

        if (isBuy) {
            // Increase position — recalculate weighted average price
            BigDecimal oldCost = position.getAveragePrice().multiply(position.getNetQuantity().abs());
            BigDecimal newCost = price.multiply(qty);
            BigDecimal newQty = position.getNetQuantity().add(qty);
            BigDecimal newAvg = newQty.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : oldCost.add(newCost).divide(newQty, 8, RoundingMode.HALF_UP);

            position.setNetQuantity(newQty);
            position.setAveragePrice(newAvg);

        } else {
            // Reduce position — realize P&L on the closed portion
            BigDecimal pnl = price.subtract(position.getAveragePrice()).multiply(qty);
            position.setRealizedPnl(position.getRealizedPnl().add(pnl));
            position.setNetQuantity(position.getNetQuantity().subtract(qty));

            // If flat, reset average price
            if (position.getNetQuantity().compareTo(BigDecimal.ZERO) == 0) {
                position.setAveragePrice(BigDecimal.ZERO);
            }
        }

        return positionRepository.save(position);
    }
}
