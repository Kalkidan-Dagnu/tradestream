import ConnectionBadge from './components/ConnectionBadge'
import Layout from './components/Layout'
import OrderFeed from './components/OrderFeed'
import { useTradeStream } from './hooks/useTradeStream'

export default function App() {
  const { trades, positions, status } = useTradeStream()

  return (
    <Layout statusBadge={<ConnectionBadge status={status} />}>
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">

        {/* P&L chart — coming in next feature */}
        <div className="xl:col-span-2 bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-64 flex items-center justify-center text-gray-600">
          PnL Chart — coming soon
        </div>

        {/* Order feed */}
        <div className="bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-64">
          <OrderFeed trades={trades} />
        </div>

        {/* Position table — coming in next feature */}
        <div className="xl:col-span-3 bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-48 flex items-center justify-center text-gray-600">
          Position Table — coming soon ({positions.size} positions)
        </div>

      </div>
    </Layout>
  )
}
