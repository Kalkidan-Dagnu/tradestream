import { useEffect, useRef, useState } from 'react'
import { Client, IMessage } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { ConnectionStatus, Position, TradeEvent } from '../types'

const WS_URL = import.meta.env.VITE_WS_URL ?? '/ws'
const MAX_TRADES = 100

export interface TradeStreamState {
  trades: TradeEvent[]
  positions: Map<string, Position>  // key: `${traderId}:${symbol}`
  status: ConnectionStatus
}

export function useTradeStream(): TradeStreamState {
  const [trades, setTrades] = useState<TradeEvent[]>([])
  const [positions, setPositions] = useState<Map<string, Position>>(new Map())
  const [status, setStatus] = useState<ConnectionStatus>('connecting')
  const clientRef = useRef<Client | null>(null)

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 3000,

      onConnect: () => {
        setStatus('connected')

        client.subscribe('/topic/trades', (msg: IMessage) => {
          const event: TradeEvent = JSON.parse(msg.body)
          setTrades(prev => [event, ...prev].slice(0, MAX_TRADES))
        })

        client.subscribe('/topic/positions', (msg: IMessage) => {
          const position: Position = JSON.parse(msg.body)
          const key = `${position.traderId}:${position.symbol}`
          setPositions(prev => new Map(prev).set(key, position))
        })
      },

      onDisconnect: () => setStatus('disconnected'),
      onStompError: () => setStatus('disconnected'),
    })

    client.activate()
    clientRef.current = client

    return () => {
      client.deactivate()
    }
  }, [])

  return { trades, positions, status }
}
