CREATE TABLE IF NOT EXISTS positions (
    id              BIGSERIAL PRIMARY KEY,
    trader_id       VARCHAR(64)    NOT NULL,
    symbol          VARCHAR(16)    NOT NULL,
    net_quantity    NUMERIC(20, 8) NOT NULL DEFAULT 0,
    average_price   NUMERIC(20, 8) NOT NULL DEFAULT 0,
    realized_pnl    NUMERIC(20, 8) NOT NULL DEFAULT 0,
    last_updated    TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_positions_trader_symbol UNIQUE (trader_id, symbol)
);

CREATE INDEX idx_positions_trader_id ON positions (trader_id);
