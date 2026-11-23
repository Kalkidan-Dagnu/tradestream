package com.tradestream.consumer.repository;

import com.tradestream.consumer.entity.TradeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {

    List<TradeEntity> findByTraderIdOrderByTradedAtDesc(String traderId, Pageable pageable);

    List<TradeEntity> findAllByOrderByTradedAtDesc(Pageable pageable);

    boolean existsByEventId(String eventId);

    @Query("SELECT SUM(t.notional) FROM TradeEntity t WHERE t.traderId = :traderId")
    java.math.BigDecimal sumNotionalByTraderId(String traderId);
}
