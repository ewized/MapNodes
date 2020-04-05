/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */

/**
 * The proxy object that will forwards the bindings to the underlying system
 *
 * @type {{ [key: string]: (...any) => any }}
 */
export const platform = new Proxy(globalThis.PLATFORM ?? {}, {
  get(target, name) {
    if (target[name]) {
      return (...args) => {
        try {
          return target[name](...args)
        } catch (any) {
          console?.error('An error has been thrown')
          console?.error(any)
        }
        return undefined
      }
    }
    console?.error(`${name} has not been defined in the platform`)
    return undefined
  },
})
