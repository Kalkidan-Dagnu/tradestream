import { useEffect, useState } from 'react'
import { api } from '../api'
import { Position, TradeEvent } from '../types'

interface InitialData {
  trades: TradeEvent[]
  positions: Map<string, Position>
  loading: boolean
  error: string | null
}

export function useInitialData(): InitialData {
  const [trades, setTrades] = useState<TradeEvent[]>([])
  const [positions, setPositions] = useState<Map<string, Position>>(new Map())
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    Promise.all([api.getTrades(50), api.getPositions()])
      .then(([fetchedTrades, fetchedPositions]) => {
        setTrades(fetchedTrades)
        const map = new Map<string, Position>()
        fetchedPositions.forEach(p => map.set(`${p.traderId}:${p.symbol}`, p))
        setPositions(map)
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  return { trades, positions, loading, error }
}
