package com.example.facemesh

import org.json.JSONObject

class ParseJson(json: String) : JSONObject(json) {
    val assets = this.optJSONArray("assets")
        ?.let {
            0.until(it.length()).map { i ->
                it.optJSONObject(i) // Returns an array of JSONObjects
            }
        }
}

class Asset(json: String) : JSONObject(json) {
    val name = this.optString("name")
    val default_scalar = this.optString("default_scalar").toFloat()
    val asset_url = this.optString("asset_url")
}