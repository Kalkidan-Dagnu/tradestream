import { ReactNode } from 'react'

interface LayoutProps {
  children: ReactNode
}

export default function Layout({ children }: LayoutProps) {
  return (
    <div className="min-h-screen bg-gray-950 text-white flex flex-col">
      {/* Header */}
      <header className="border-b border-gray-800 px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <span className="text-green-400 font-bold text-xl tracking-tight">TradeStream</span>
          <span className="text-gray-500 text-sm">live copy-trading dashboard</span>
        </div>
        <div id="connection-status" className="flex items-center gap-2 text-sm text-gray-400">
          {/* Connection indicator injected by useTradeStream */}
        </div>
      </header>

      {/* Main content */}
      <main className="flex-1 p-6">
        {children}
      </main>

      <footer className="border-t border-gray-800 px-6 py-3 text-xs text-gray-600">
        TradeStream — built by Kalkidan Dagnu
      </footer>
    </div>
  )
}
