/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import js from './bindings_js.js'
import bindings from './bindings.js'


// replace circular references so we dont error out in var_dump
const replaceCircular = (obj, level = 0, already = new WeakSet()) => {
  if (typeof obj === 'object') {
    if (!obj) return obj
    if (already.has(obj)) {
      return 'CIRCULAR'
    }
    already.add(obj)
    if (Array.isArray(obj)) {
      return obj.map((item) => replaceCircular(item, level + 1, already))
    }
    const newObj = {}
    Object.keys(obj).forEach((key) => {
      const val = replaceCircular(obj[key], level + 1, already)
      newObj[key] = val
    })
    already.delete(obj)
    return newObj
  }
  return obj
}

/** Dump the var to the screen */
export const var_dump = (variable) => console.log(JSON.stringify(replaceCircular(variable)))

/** The two way binding system */
export const $ = { js, bindings }
