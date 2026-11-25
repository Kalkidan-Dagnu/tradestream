import { useEffect, useState } from 'react'
import { Position } from '../types'

interface Alert {
  id: string
  message: string
  type: 'warning' | 'danger'
  timestamp: Date
}

interface AlertBannerProps {
  positions: Map<string, Position>
  pnlThreshold?: number   // alert if any trader's realized P&L drops below this (default -100)
  drawdownPct?: number    // alert if net qty swings this % from avg (default 20%)
}

const AUTO_DISMISS_MS = 8000

export default function AlertBanner({
  positions,
  pnlThreshold = -100,
}: AlertBannerProps) {
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [dismissed, setDismissed] = useState<Set<string>>(new Set())

  // Check positions for threshold breaches whenever positions update
  useEffect(() => {
    const newAlerts: Alert[] = []

    positions.forEach(pos => {
      const pnl = Number(pos.realizedPnl)
      const key = `pnl:${pos.traderId}:${pos.symbol}`

      if (pnl < pnlThreshold && !dismissed.has(key)) {
        newAlerts.push({
          id: key,
          message: `${pos.traderId} — ${pos.symbol} realized P&L dropped to $${pnl.toFixed(2)}`,
          type: 'danger',
          timestamp: new Date(),
        })
      }
    })

    if (newAlerts.length > 0) {
      setAlerts(prev => {
        const existingIds = new Set(prev.map(a => a.id))
        const fresh = newAlerts.filter(a => !existingIds.has(a.id))
        return [...prev, ...fresh]
      })
    }
  }, [positions, pnlThreshold])

  // Auto-dismiss after timeout
  useEffect(() => {
    if (alerts.length === 0) return
    const timer = setTimeout(() => {
      setAlerts(prev => prev.slice(1))
    }, AUTO_DISMISS_MS)
    return () => clearTimeout(timer)
  }, [alerts])

  function dismiss(id: string) {
    setDismissed(prev => new Set(prev).add(id))
    setAlerts(prev => prev.filter(a => a.id !== id))
  }

  if (alerts.length === 0) return null

  return (
    <div className="fixed bottom-6 right-6 z-50 flex flex-col gap-2 max-w-sm w-full">
      {alerts.map(alert => (
        <div
          key={alert.id}
          className={`flex items-start justify-between gap-3 rounded-xl px-4 py-3 shadow-lg border text-sm
            ${alert.type === 'danger'
              ? 'bg-red-950 border-red-800 text-red-300'
              : 'bg-yellow-950 border-yellow-800 text-yellow-300'
            }`}
        >
          <div>
            <div className="font-semibold mb-0.5">
              {alert.type === 'danger' ? '⚠ P&L Alert' : '⚡ Warning'}
            </div>
            <div className="text-xs opacity-80">{alert.message}</div>
          </div>
          <button
            onClick={() => dismiss(alert.id)}
            className="text-gray-500 hover:text-white mt-0.5 shrink-0"
            aria-label="Dismiss"
          >
            ✕
          </button>
        </div>
      ))}
    </div>
  )
}
