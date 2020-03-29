/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import _ from 'lodash'

import { is_object, not_null } from '../conditions.js'


/** Represents a team from the json object */
export default class JsonObject {
  /** Make sure the JsonObject has the id and the object */
  constructor(id, json) {
    this._id = not_null(id, 'id')
    this._json = not_null(json, 'json')
  }

  /** Get the id of this JsonObject, also makes the id lowercase */
  get id() {
    return _.lowerCase(this._id)
  }

  /** Get the name for the object defaults to id */
  get name() {
    return this._id
  }

  /**
   * Will verify that the JSON object matches the provided schema.
   *
   * This will take the Object from the static schema property can run checks on
   * it. In this case its better to use the get keyword to avoid evaluation of the
   * Object before its needed.
   *
   * Example:
   *
   * static get schema() {
   *   return {
   *     key: { type: boolean, value: false },
   *     foo: { type: String, value: 'bar' }
   *   }
   * }
   *
   * @param json the json to validate the schema on, defaults to the internal json
   * @param schema defaults to the static schema property
   * @return boolean
   */
  verify(json = this._json, schema = this.constructor.schema) {
    is_object(not_null(json, 'Json must exist'), 'Must be a JSON object')
    not_null(schema, 'Each object must have a schema associated with it')
    // todo verify that the json matches the given schema more verbose checking
    // return _.keys(json).reduce((and, key) => {
    //   const value = json[key]
    //   const { type } = schema[key]
    //   const types = Array.isArray(type) ? type : [type]
    //   // eslint-disable-next-line valid-typeof
    //   return and && types.reduce((or, type) => or || (type === 'array' ? Array.isArray(value) : (type !== undefined && typeof value === type)), false)
    // }, true)
    return true
  }

  /** The JSON object of this object */
  toJSON() {
    return this._json
  }
}