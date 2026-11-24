import { ReactNode } from 'react'

interface LayoutProps {
  children: ReactNode
  statusBadge?: ReactNode
}

export default function Layout({ children, statusBadge }: LayoutProps) {
  return (
    <div className="min-h-screen bg-gray-950 text-white flex flex-col">
      <header className="border-b border-gray-800 px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <span className="text-green-400 font-bold text-xl tracking-tight">TradeStream</span>
          <span className="text-gray-500 text-sm">live copy-trading dashboard</span>
        </div>
        {statusBadge}
      </header>

      <main className="flex-1 p-6">
        {children}
      </main>

      <footer className="border-t border-gray-800 px-6 py-3 text-xs text-gray-600">
        TradeStream — built by Kalkidan Dagnu
      </footer>
    </div>
  )
}
