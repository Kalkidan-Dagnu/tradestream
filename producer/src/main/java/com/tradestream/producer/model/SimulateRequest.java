package com.tradestream.producer.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SimulateRequest {

    @Min(1) @Max(10)
    private int traders = 3;

    @Min(1) @Max(100)
    private int followers = 10;

    @Min(5) @Max(300)
    private int durationSeconds = 60;
}
