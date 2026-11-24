import { TradeEvent } from '../types'

interface OrderFeedProps {
  trades: TradeEvent[]
}

export default function OrderFeed({ trades }: OrderFeedProps) {
  return (
    <div className="flex flex-col h-full">
      <h2 className="text-sm font-semibold text-gray-400 uppercase tracking-wider mb-3">
        Order Feed
      </h2>

      {trades.length === 0 ? (
        <div className="flex-1 flex items-center justify-center text-gray-600 text-sm">
          Waiting for trades…
        </div>
      ) : (
        <ul className="flex-1 overflow-y-auto space-y-1 pr-1">
          {trades.map((trade) => (
            <li
              key={trade.eventId}
              className="flex items-center justify-between text-xs rounded-lg px-3 py-2 bg-gray-800 hover:bg-gray-750 transition-colors"
            >
              {/* Left: side badge + symbol */}
              <div className="flex items-center gap-2 min-w-0">
                <span className={`w-10 text-center rounded px-1 py-0.5 font-bold text-xs ${
                  trade.side === 'BUY'
                    ? 'bg-green-900 text-green-400'
                    : 'bg-red-900 text-red-400'
                }`}>
                  {trade.side}
                </span>
                <span className="text-white font-medium">{trade.symbol}</span>
                <span className="text-gray-500 truncate">{trade.traderId}</span>
              </div>

              {/* Right: qty @ price */}
              <div className="text-right shrink-0 ml-2">
                <span className="text-gray-300">{Number(trade.quantity).toFixed(4)}</span>
                <span className="text-gray-600 mx-1">@</span>
                <span className="text-gray-300">${Number(trade.price).toLocaleString()}</span>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
