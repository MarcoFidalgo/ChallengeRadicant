package com.stockmarket.repository

import com.stockmarket.model.HistoricalData
import com.stockmarket.model.Stock
import com.stockmarket.model.StockDetails
import com.stockmarket.model.api.FMPApiService
import com.stockmarket.utils.Constants.FMP_API_KEY

class StockRepository(private val apiService: FMPApiService) {

    suspend fun searchStocks(query: String): List<Stock> {
        val response = apiService.searchStocks(query = query, apiKey = FMP_API_KEY)
        return response
    }

    suspend fun getStockDetails(symbol: String): StockDetails {
        return apiService.getStockDetails(symbol = symbol, apiKey = FMP_API_KEY).first()
    }

    suspend fun getHistoricalData(
        symbol: String,
        from: String?,
        to: String?
    ): HistoricalData {
        return apiService.getHistoricalData(
            symbol = symbol,
            from = from,
            to = to,
            apiKey = FMP_API_KEY
        )
    }
}