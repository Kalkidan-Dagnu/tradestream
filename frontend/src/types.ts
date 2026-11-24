export interface TradeEvent {
  eventId: string
  traderId: string
  symbol: string
  side: 'BUY' | 'SELL'
  quantity: number
  price: number
  notional: number
  exchange: string
  timestamp: string
}

export interface Position {
  id: number
  traderId: string
  symbol: string
  netQuantity: number
  averagePrice: number
  realizedPnl: number
  lastUpdated: string
}

export type ConnectionStatus = 'connecting' | 'connected' | 'disconnected'
