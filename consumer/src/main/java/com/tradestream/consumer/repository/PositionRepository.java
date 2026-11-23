package com.tradestream.consumer.repository;

import com.tradestream.consumer.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

    List<PositionEntity> findByTraderId(String traderId);

    Optional<PositionEntity> findByTraderIdAndSymbol(String traderId, String symbol);

    @Query("SELECT SUM(p.realizedPnl) FROM PositionEntity p WHERE p.traderId = :traderId")
    BigDecimal sumRealizedPnlByTraderId(String traderId);

    @Query("SELECT SUM(p.realizedPnl) FROM PositionEntity p")
    BigDecimal sumTotalRealizedPnl();
}
