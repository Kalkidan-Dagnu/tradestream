import { useEffect, useState } from 'react'
import AlertBanner from './components/AlertBanner'
import ConnectionBadge from './components/ConnectionBadge'
import Layout from './components/Layout'
import OrderFeed from './components/OrderFeed'
import PnLChart from './components/PnLChart'
import PositionTable from './components/PositionTable'
import { useInitialData } from './hooks/useInitialData'
import { useTradeStream } from './hooks/useTradeStream'
import { Position, TradeEvent } from './types'

const MAX_TRADES = 100

export default function App() {
  const { trades: initialTrades, positions: initialPositions, loading } = useInitialData()
  const { trades: liveTrades, positions: livePositions, status } = useTradeStream()

  const [trades, setTrades] = useState<TradeEvent[]>([])
  const [positions, setPositions] = useState<Map<string, Position>>(new Map())

  useEffect(() => {
    if (!loading) {
      setTrades(initialTrades)
      setPositions(initialPositions)
    }
  }, [loading])

  useEffect(() => {
    if (liveTrades.length > 0) {
      setTrades(prev => [liveTrades[0], ...prev].slice(0, MAX_TRADES))
    }
  }, [liveTrades])

  useEffect(() => {
    if (livePositions.size > 0) {
      setPositions(prev => new Map([...prev, ...livePositions]))
    }
  }, [livePositions])

  return (
    <Layout statusBadge={<ConnectionBadge status={status} />}>
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">

        <div className="xl:col-span-2 bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-64">
          <PnLChart positions={positions} />
        </div>

        <div className="bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-64">
          <OrderFeed trades={trades} />
        </div>

        <div className="xl:col-span-3 bg-gray-900 rounded-xl border border-gray-800 p-5">
          <PositionTable positions={positions} />
        </div>

      </div>

      {/* Fixed bottom-right alert overlay */}
      <AlertBanner positions={positions} pnlThreshold={-100} />
    </Layout>
  )
}
