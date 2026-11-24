import Layout from './components/Layout'

export default function App() {
  return (
    <Layout>
      {/* Dashboard grid — components will be filled in subsequent features */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">

        {/* Top row: P&L chart spans 2 columns */}
        <div className="xl:col-span-2 bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-64 flex items-center justify-center text-gray-600">
          PnL Chart — coming soon
        </div>

        {/* Top right: Order feed */}
        <div className="bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-64 flex items-center justify-center text-gray-600">
          Order Feed — coming soon
        </div>

        {/* Bottom: Position table spans full width */}
        <div className="xl:col-span-3 bg-gray-900 rounded-xl border border-gray-800 p-5 min-h-48 flex items-center justify-center text-gray-600">
          Position Table — coming soon
        </div>

      </div>
    </Layout>
  )
}
