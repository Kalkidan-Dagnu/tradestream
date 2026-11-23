CREATE TABLE IF NOT EXISTS trades (
    id          BIGSERIAL PRIMARY KEY,
    event_id    VARCHAR(64)    NOT NULL UNIQUE,
    trader_id   VARCHAR(64)    NOT NULL,
    symbol      VARCHAR(16)    NOT NULL,
    side        VARCHAR(8)     NOT NULL,
    quantity    NUMERIC(20, 8) NOT NULL,
    price       NUMERIC(20, 8) NOT NULL,
    notional    NUMERIC(20, 8) NOT NULL,
    exchange    VARCHAR(32),
    traded_at   TIMESTAMPTZ    NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trades_trader_id  ON trades (trader_id);
CREATE INDEX idx_trades_symbol     ON trades (symbol);
CREATE INDEX idx_trades_traded_at  ON trades (traded_at DESC);
