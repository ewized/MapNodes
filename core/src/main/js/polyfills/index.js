/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import * as console_exports from './polyfills/console.js'
import * as var_dump_exports from './polyfills/var_dump.js'


// Inject the exports into the globalThis variable
[console_exports, var_dump_exports].forEach((exports) => {
  Object.entries(exports).forEach(([key, value]) => {
    try {
      globalThis[key] = value
    } catch (error) {
      console?.error(error)
    }
  })
})
