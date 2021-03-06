/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */

/** The command executor is the one who is executating the commands */
export default class CommandExecutor {
  constructor(executor) {
    this._executor = executor // the executor is an instance of a player
  }

  /** Send the message to the executor */
  send_message(message) {
    if (this._executor) {
      return this._executor.send_message(message)
    }
    return console.log(message)
  }

  /** Does the executor has a permission */
  has_permission(permission) {
    if (this._executor) {
      return this._executor.has_permission(permission)
    }
    return true
  }

  /** Get the executor of the command may be null */
  get player() {
    return this._executor
  }

  /** Is the executor a player */
  is_player() {
    return this._executor != null
  }

  /** Is the executor the console */
  is_console() {
    return this._executor == null
  }
}
