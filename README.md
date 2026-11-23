# TradeStream

[![CI](https://github.com/Kalkidan-Dagnu/tradestream/actions/workflows/ci.yml/badge.svg)](https://github.com/Kalkidan-Dagnu/tradestream/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Real-time crypto copy-trading platform. Expert traders place orders; follower accounts copy them proportionally. Every trade flows through Kafka, gets processed by a position aggregator, and streams live to a React dashboard via WebSocket.

```
Expert Trader → Producer (REST) → Kafka → Consumer → PostgreSQL
                                              │
                                         WebSocket
                                              │
                                     React Dashboard
                               (live P&L · orders · positions)
```

## Quickstart

```bash
cp .env.example .env
docker compose up --build
```

| Service    | URL                              |
|------------|----------------------------------|
| Dashboard  | http://localhost:3000            |
| Producer   | http://localhost:8081/actuator   |
| Consumer   | http://localhost:8082/actuator   |

Trigger a simulated trade stream:

```bash
curl -X POST http://localhost:8081/api/simulate \
  -H "Content-Type: application/json" \
  -d '{"traders": 3, "followers": 10, "durationSeconds": 60}'
```

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│  React Dashboard (port 3000)                                 │
│  PnLChart · OrderFeed · PositionTable · AlertBanner          │
└──────────────────────┬───────────────────────────────────────┘
                       │ WebSocket /ws
┌──────────────────────▼───────────────────────────────────────┐
│  Consumer Service (port 8082) — Spring Boot                  │
│  Kafka Listener → Position Aggregator → PostgreSQL           │
│  WebSocket Broadcaster → REST API /api/**                    │
│  Prometheus metrics /actuator/prometheus                     │
└──────────────────────┬───────────────────────────────────────┘
                       │ Kafka (trade-events topic)
┌──────────────────────▼───────────────────────────────────────┐
│  Producer Service (port 8081) — Spring Boot                  │
│  POST /api/trades   — publish a single trade event           │
│  POST /api/simulate — start a simulated trade stream         │
└──────────────────────────────────────────────────────────────┘
```

## Project Structure

```
tradestream/
├── producer/          # Spring Boot — Kafka publisher + simulation endpoint
├── consumer/          # Spring Boot — Kafka consumer, aggregator, WebSocket
├── frontend/          # React + TypeScript + Recharts + TailwindCSS
├── k8s/               # Kubernetes manifests
├── monitoring/        # Grafana dashboard JSON
├── docker-compose.yml
└── .github/workflows/ci.yml
```

## Stack

**Backend:** Java 17 · Spring Boot 3.3 · Spring Kafka · Spring WebSocket · PostgreSQL · Flyway · Micrometer + Prometheus · Testcontainers

**Frontend:** React 18 · TypeScript · Vite · Recharts · TailwindCSS

**Infrastructure:** Docker Compose · Kafka (Confluent 7.7) · Kubernetes · GitHub Actions

## License

MIT
