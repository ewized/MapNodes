/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import { platform } from '../platform.js'


/**
 * Include our own not_null since polyfills should be self contained
 * @template T
 * @param {T} value
 * @param {string} [message]
 * @return {T}
 */
const not_null = (value, message) => {
  if (!value) {
    throw new Error(message ?? 'value must exist')
  }
  return value
}

// Impelemented from the docs https://developer.mozilla.org/en-US/docs/Web/API/WebSocket
export class WebSocket {
  static CONNECTING = 0
  static OPEN = 1
  static CLOSING = 2
  static CLOSED = 3

  _readyState = WebSocket.CONNECTING
  _handlers = {}

  /**
   * @param {String} url
   * @param {String | String[]} [protocols]
   */
  constructor(url, protocols) {
    this._url = not_null(url, 'url must exist')
    this._protocols = [protocols ?? ''].flat()
  }

  _open() {
    // todo figure out what to do for function
    const { on_open, on_message, on_close, on_error, ...connection } = platform.create_web_socket(this.url, this._protocols)
    this._connection = connection
    on_open((event) => {
      this._readyState = WebSocket.OPEN
      this._handlers?.onopen(event)
    })
    on_message((event) => this._handlers?.onopen(event))
    on_error((event) => this._handlers?.onopen(event))
    on_close((event) => {
      this._readyState = WebSocket.CLOSING
      this._handlers?.on_close(event)
      this._readyState = WebSocket.CLOSED
    })
    return this
  }

  /**
   * @param {number} [code]
   * @param {string} [reason]
   */
  close(code = 1005, reason) {
    if (this.readyState === WebSocket.OPEN) {
      // todo close the web_socket
      this._connection?.close(code, reason)
    }
  }

  /**
   * @param {string|Blob|ArrayBuffer|ArrayBufferView} data
   */
  send(data) {
    // todo send data over the connection
    this._connection?.send(not_null(data, 'data must exist'))
  }

  set onclose(listener) {
    this._handlers.onclose = listener
  }

  set onerror(listener) {
    this._handlers.onerror = listener
  }

  set onmessage(listener) {
    this._handlers.onmessage = listener
  }

  set onopen(listener) {
    this._handlers.onopen = listener
  }

  get protocol() {
    return this.readyState === WebSocket.CONNECTING ? null : this._protocols[0]
  }

  get readyState() {
    return this._readyState
  }

  get url() {
    return this._url
  }
}
