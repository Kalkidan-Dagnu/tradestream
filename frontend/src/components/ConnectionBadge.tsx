import { ConnectionStatus } from '../types'

const config: Record<ConnectionStatus, { dot: string; label: string }> = {
  connected:    { dot: 'bg-green-400',  label: 'Live' },
  connecting:   { dot: 'bg-yellow-400 animate-pulse', label: 'Connecting' },
  disconnected: { dot: 'bg-red-500',   label: 'Disconnected' },
}

export default function ConnectionBadge({ status }: { status: ConnectionStatus }) {
  const { dot, label } = config[status]
  return (
    <div className="flex items-center gap-2 text-sm text-gray-400">
      <span className={`w-2 h-2 rounded-full ${dot}`} />
      {label}
    </div>
  )
}
