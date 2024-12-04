package com.stockmarket.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Stock(
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "name")
    var longName: String?
)

@JsonClass(generateAdapter = true)
data class StockDetails(
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "name")
    var longName: String?,
    @Json(name = "previousClose")
    var lastClosingPrice: Double?,

    val yearToDateReturn: Double?
)

@JsonClass(generateAdapter = true)
data class HistoricalData(
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "historical")
    val historical: List<HistoricalPrice>
)

@JsonClass(generateAdapter = true)
data class HistoricalPrice(
    @Json(name = "date")
    val date: String,
    @Json(name = "close")
    val close: Double
)

