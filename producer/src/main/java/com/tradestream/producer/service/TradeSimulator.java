package com.tradestream.producer.service;

import com.tradestream.producer.model.OrderSide;
import com.tradestream.producer.model.SimulateRequest;
import com.tradestream.producer.model.Symbol;
import com.tradestream.producer.model.TradeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeSimulator {

    private final TradeEventPublisher publisher;
    private final Random random = new Random();

    // Realistic mid-prices for each symbol (approximate)
    private static final Map<Symbol, BigDecimal> BASE_PRICES = Map.of(
            Symbol.BTCUSDT,  new BigDecimal("95000"),
            Symbol.ETHUSDT,  new BigDecimal("3400"),
            Symbol.SOLUSDT,  new BigDecimal("220"),
            Symbol.BNBUSDT,  new BigDecimal("640"),
            Symbol.XRPUSDT,  new BigDecimal("2.40")
    );

    private final AtomicBoolean simulationRunning = new AtomicBoolean(false);

    /**
     * Starts a simulation in the background. Returns immediately.
     * Fires trades at random intervals (500ms–2s) for the requested duration.
     */
    public void startSimulation(SimulateRequest request) {
        if (simulationRunning.getAndSet(true)) {
            log.warn("Simulation already running — ignoring new request");
            return;
        }

        List<String> traderIds = buildTraderIds(request.getTraders());
        AtomicInteger totalPublished = new AtomicInteger(0);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Schedule the simulation end
        scheduler.schedule(() -> {
            simulationRunning.set(false);
            scheduler.shutdown();
            log.info("Simulation ended. Total events published: {}", totalPublished.get());
        }, request.getDurationSeconds(), TimeUnit.SECONDS);

        // Schedule periodic trade generation
        scheduleNextTrade(scheduler, traderIds, totalPublished, request.getDurationSeconds() * 1000L);
    }

    private void scheduleNextTrade(ScheduledExecutorService scheduler,
                                   List<String> traderIds,
                                   AtomicInteger count,
                                   long endTimeMs) {
        if (!simulationRunning.get()) return;

        long delayMs = 500 + random.nextInt(1500); // 500ms – 2000ms between trades

        scheduler.schedule(() -> {
            if (simulationRunning.get()) {
                TradeEvent event = generateRandomTrade(traderIds);
                publisher.publish(event);
                count.incrementAndGet();
                scheduleNextTrade(scheduler, traderIds, count, endTimeMs);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    private TradeEvent generateRandomTrade(List<String> traderIds) {
        String traderId = traderIds.get(random.nextInt(traderIds.size()));
        Symbol symbol = Symbol.values()[random.nextInt(Symbol.values().length)];
        OrderSide side = random.nextBoolean() ? OrderSide.BUY : OrderSide.SELL;

        BigDecimal basePrice = BASE_PRICES.get(symbol);
        // Add ±0.5% random price deviation
        double deviation = 1.0 + (random.nextDouble() - 0.5) * 0.01;
        BigDecimal price = basePrice.multiply(BigDecimal.valueOf(deviation))
                .setScale(2, RoundingMode.HALF_UP);

        // Quantity: small for high-priced assets, larger for low-priced
        BigDecimal quantity = generateQuantity(symbol);

        return TradeEvent.builder()
                .traderId(traderId)
                .symbol(symbol)
                .side(side)
                .quantity(quantity)
                .price(price)
                .exchange("BINANCE")
                .build();
    }

    private BigDecimal generateQuantity(Symbol symbol) {
        return switch (symbol) {
            case BTCUSDT  -> randomDecimal(0.001, 0.1, 4);
            case ETHUSDT  -> randomDecimal(0.01, 1.0, 4);
            case SOLUSDT  -> randomDecimal(0.1, 10.0, 2);
            case BNBUSDT  -> randomDecimal(0.05, 5.0, 3);
            case XRPUSDT  -> randomDecimal(10, 1000, 0);
        };
    }

    private BigDecimal randomDecimal(double min, double max, int scale) {
        double value = min + (max - min) * random.nextDouble();
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);
    }

    private List<String> buildTraderIds(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> "trader_" + String.format("%03d", i))
                .toList();
    }

    public boolean isRunning() {
        return simulationRunning.get();
    }
}
