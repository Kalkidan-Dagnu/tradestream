import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { Position } from '../types'

interface PnLChartProps {
  positions: Map<string, Position>
}

interface DataPoint {
  time: string
  pnl: number
}

function buildChartData(positions: Map<string, Position>): DataPoint[] {
  // Group realized P&L snapshots by lastUpdated time, cumulative across all traders
  const byTime = new Map<string, number>()

  positions.forEach(pos => {
    const time = new Date(pos.lastUpdated).toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    })
    byTime.set(time, (byTime.get(time) ?? 0) + Number(pos.realizedPnl))
  })

  return Array.from(byTime.entries())
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([time, pnl]) => ({ time, pnl: Math.round(pnl * 100) / 100 }))
}

function fmt(value: number) {
  return `$${value.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

export default function PnLChart({ positions }: PnLChartProps) {
  const data = buildChartData(positions)
  const totalPnl = Array.from(positions.values())
    .reduce((sum, p) => sum + Number(p.realizedPnl), 0)
  const isPositive = totalPnl >= 0

  return (
    <div className="flex flex-col h-full">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-sm font-semibold text-gray-400 uppercase tracking-wider">
          Realized P&L
        </h2>
        <span className={`text-lg font-bold ${isPositive ? 'text-green-400' : 'text-red-400'}`}>
          {isPositive ? '+' : ''}{fmt(totalPnl)}
        </span>
      </div>

      {data.length < 2 ? (
        <div className="flex-1 flex items-center justify-center text-gray-600 text-sm">
          Waiting for trade data…
        </div>
      ) : (
        <div className="flex-1 min-h-0">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={data} margin={{ top: 4, right: 8, left: 8, bottom: 0 }}>
              <defs>
                <linearGradient id="pnlGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor={isPositive ? '#4ade80' : '#f87171'} stopOpacity={0.3} />
                  <stop offset="95%" stopColor={isPositive ? '#4ade80' : '#f87171'} stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
              <XAxis
                dataKey="time"
                tick={{ fill: '#6b7280', fontSize: 11 }}
                tickLine={false}
                axisLine={false}
                interval="preserveStartEnd"
              />
              <YAxis
                tick={{ fill: '#6b7280', fontSize: 11 }}
                tickLine={false}
                axisLine={false}
                tickFormatter={v => `$${v}`}
                width={60}
              />
              <Tooltip
                contentStyle={{ backgroundColor: '#111827', border: '1px solid #374151', borderRadius: 8 }}
                labelStyle={{ color: '#9ca3af', fontSize: 11 }}
                formatter={(value: number) => [fmt(value), 'Realized P&L']}
              />
              <Area
                type="monotone"
                dataKey="pnl"
                stroke={isPositive ? '#4ade80' : '#f87171'}
                strokeWidth={2}
                fill="url(#pnlGradient)"
                dot={false}
                activeDot={{ r: 4 }}
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  )
}
