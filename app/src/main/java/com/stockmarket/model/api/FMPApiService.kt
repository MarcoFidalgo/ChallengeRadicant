package com.stockmarket.model.api

import com.stockmarket.model.HistoricalData
import com.stockmarket.model.Stock
import com.stockmarket.model.StockDetails
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FMPApiService {
    @GET("search")
    suspend fun searchStocks(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20,
        @Query("apikey") apiKey: String
    ): List<Stock>

    @GET("quote/{symbol}")
    suspend fun getStockDetails(
        @Path("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): List<StockDetails>

    @GET("historical-price-full/{symbol}")
    suspend fun getHistoricalData(
        @Path("symbol") symbol: String,
        @Query("from") from: String?,
        @Query("to") to: String?,
        @Query("apikey") apiKey: String
    ): HistoricalData
}