import { Position } from '../types'

interface PositionTableProps {
  positions: Map<string, Position>
}

function pnlColor(value: number) {
  if (value > 0) return 'text-green-400'
  if (value < 0) return 'text-red-400'
  return 'text-gray-400'
}

function fmt(value: number, decimals = 2) {
  return Number(value).toLocaleString(undefined, {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  })
}

export default function PositionTable({ positions }: PositionTableProps) {
  const rows = Array.from(positions.values()).sort((a, b) =>
    a.traderId.localeCompare(b.traderId) || a.symbol.localeCompare(b.symbol)
  )

  return (
    <div>
      <h2 className="text-sm font-semibold text-gray-400 uppercase tracking-wider mb-3">
        Positions
      </h2>

      {rows.length === 0 ? (
        <div className="flex items-center justify-center h-24 text-gray-600 text-sm">
          No open positions yet
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-xs">
            <thead>
              <tr className="text-gray-500 border-b border-gray-800">
                <th className="text-left pb-2 pr-4">Trader</th>
                <th className="text-left pb-2 pr-4">Symbol</th>
                <th className="text-right pb-2 pr-4">Net Qty</th>
                <th className="text-right pb-2 pr-4">Avg Price</th>
                <th className="text-right pb-2 pr-4">Realized P&L</th>
                <th className="text-right pb-2">Last Updated</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((pos) => (
                <tr
                  key={`${pos.traderId}:${pos.symbol}`}
                  className="border-b border-gray-800 hover:bg-gray-800 transition-colors"
                >
                  <td className="py-2 pr-4 text-gray-300">{pos.traderId}</td>
                  <td className="py-2 pr-4 font-medium text-white">{pos.symbol}</td>
                  <td className={`py-2 pr-4 text-right ${Number(pos.netQuantity) >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                    {fmt(Number(pos.netQuantity), 4)}
                  </td>
                  <td className="py-2 pr-4 text-right text-gray-300">
                    ${fmt(Number(pos.averagePrice))}
                  </td>
                  <td className={`py-2 pr-4 text-right font-semibold ${pnlColor(Number(pos.realizedPnl))}`}>
                    {Number(pos.realizedPnl) >= 0 ? '+' : ''}${fmt(Number(pos.realizedPnl))}
                  </td>
                  <td className="py-2 text-right text-gray-600">
                    {new Date(pos.lastUpdated).toLocaleTimeString()}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
