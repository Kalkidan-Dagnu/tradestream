import { useEffect, useState } from 'react'
import ConnectionBadge from './components/ConnectionBadge'
import Layout from './components/Layout'
import OrderFeed from './components/OrderFeed'
import PositionTable from './components/PositionTable'
import { useInitialData } from './hooks/useInitialData'
import { useTradeStream } from './hooks/useTradeStream'
import { Position, TradeEvent } from './types'

const MAX_TRADES = 100

export default function App() {
  const { trades: initialTrades, positions: initialPositions, loading } = useInitialData()
  const { trades: liveTrades, positions: livePositions, status } = useTradeStream()

  // Merge: live WebSocket events on top of initial REST data
  const [trades, setTrades] = useState<TradeEvent[]>([])
  const [positions, setPositions] = useState<Map<string, Position>>(new Map())

  // Seed from REST on first load
  useEffect(() => {
    if (!loading) {
      setTrades(initialTrades)
      setPositions(initialPositions)
    }
  }, [loading])

  // Prepend new live trades
  useEffect(() => {
    if (liveTrades.length > 0) {
      setTrades(prev => [liveTrades[0], ...prev].slice(0, MAX_TRADES))
    }
  }, [liveTrades])

  // Merge live position updates
  useEffect(() => {
    if (livePositions.size > 0) {
      setPositions(prev => new Map([...prev, ...livePositions]))
    }
  }, [livePositions])

  return (
    <Layout statusBadge={<ConnectionBadge status={status} />}>
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">

        <div className="xl:col-span-2 bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-64 flex items-center justify-center text-gray-600">
          PnL Chart — coming soon
        </div>

        <div className="bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-64">
          <OrderFeed trades={trades} />
        </div>

        <div className="xl:col-span-3 bg-gray-900 rounded-xl border border-gray-800 p-5">
          <PositionTable positions={positions} />
        </div>

      </div>
    </Layout>
  )
}
