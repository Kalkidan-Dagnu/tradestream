package com.tradestream.producer.controller;

import com.tradestream.producer.model.SimulateRequest;
import com.tradestream.producer.service.TradeSimulator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simulate")
@RequiredArgsConstructor
public class SimulationController {

    private final TradeSimulator simulator;

    /**
     * Start a simulated trade stream.
     * POST /api/simulate
     * Body: { "traders": 3, "followers": 10, "durationSeconds": 60 }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> startSimulation(@Valid @RequestBody SimulateRequest request) {
        if (simulator.isRunning()) {
            return ResponseEntity.ok(Map.of(
                    "status", "already_running",
                    "message", "A simulation is already in progress"
            ));
        }

        simulator.startSimulation(request);

        return ResponseEntity.accepted().body(Map.of(
                "status", "started",
                "traders", request.getTraders(),
                "followers", request.getFollowers(),
                "durationSeconds", request.getDurationSeconds()
        ));
    }

    /**
     * Check simulation status.
     * GET /api/simulate/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of("running", simulator.isRunning()));
    }
}
