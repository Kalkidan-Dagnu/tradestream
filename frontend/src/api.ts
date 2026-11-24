import { Position, TradeEvent } from '../types'

const BASE_URL = import.meta.env.VITE_API_URL ?? '/api'

async function get<T>(path: string): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`)
  if (!res.ok) throw new Error(`API error ${res.status}: ${path}`)
  return res.json() as Promise<T>
}

export const api = {
  getTrades: (limit = 50) =>
    get<TradeEvent[]>(`/trades?limit=${limit}`),

  getTradesByTrader: (traderId: string, limit = 50) =>
    get<TradeEvent[]>(`/trades/${traderId}?limit=${limit}`),

  getPositions: () =>
    get<Position[]>(`/positions`),

  getPositionsByTrader: (traderId: string) =>
    get<Position[]>(`/positions/${traderId}`),

  getTotalPnl: () =>
    get<{ totalRealizedPnl: number }>(`/pnl`),

  getTraderPnl: (traderId: string) =>
    get<{ realizedPnl: number }>(`/pnl/${traderId}`),
}
