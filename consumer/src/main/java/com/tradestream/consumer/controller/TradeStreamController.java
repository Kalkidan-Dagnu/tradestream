package com.tradestream.consumer.controller;

import com.tradestream.consumer.entity.PositionEntity;
import com.tradestream.consumer.entity.TradeEntity;
import com.tradestream.consumer.repository.PositionRepository;
import com.tradestream.consumer.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradeStreamController {

    private final TradeRepository tradeRepository;
    private final PositionRepository positionRepository;

    /** GET /api/trades?limit=50 — most recent trades across all traders */
    @GetMapping("/trades")
    public ResponseEntity<List<TradeEntity>> getTrades(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(
                tradeRepository.findAllByOrderByTradedAtDesc(PageRequest.of(0, limit)));
    }

    /** GET /api/trades/{traderId}?limit=50 — trades for a specific trader */
    @GetMapping("/trades/{traderId}")
    public ResponseEntity<List<TradeEntity>> getTradesByTrader(
            @PathVariable String traderId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(
                tradeRepository.findByTraderIdOrderByTradedAtDesc(traderId, PageRequest.of(0, limit)));
    }

    /** GET /api/positions — all open positions */
    @GetMapping("/positions")
    public ResponseEntity<List<PositionEntity>> getPositions() {
        return ResponseEntity.ok(positionRepository.findAll());
    }

    /** GET /api/positions/{traderId} — positions for a specific trader */
    @GetMapping("/positions/{traderId}")
    public ResponseEntity<List<PositionEntity>> getPositionsByTrader(
            @PathVariable String traderId) {
        return ResponseEntity.ok(positionRepository.findByTraderId(traderId));
    }

    /** GET /api/pnl — total realized P&L across all traders */
    @GetMapping("/pnl")
    public ResponseEntity<Map<String, BigDecimal>> getTotalPnl() {
        BigDecimal total = positionRepository.sumTotalRealizedPnl();
        return ResponseEntity.ok(Map.of("totalRealizedPnl", total != null ? total : BigDecimal.ZERO));
    }

    /** GET /api/pnl/{traderId} — realized P&L for a specific trader */
    @GetMapping("/pnl/{traderId}")
    public ResponseEntity<Map<String, BigDecimal>> getTraderPnl(@PathVariable String traderId) {
        BigDecimal pnl = positionRepository.sumRealizedPnlByTraderId(traderId);
        return ResponseEntity.ok(Map.of("realizedPnl", pnl != null ? pnl : BigDecimal.ZERO));
    }
}
