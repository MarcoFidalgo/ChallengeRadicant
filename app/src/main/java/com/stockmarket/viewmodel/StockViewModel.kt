package com.stockmarket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockmarket.model.HistoricalData
import com.stockmarket.model.Stock
import com.stockmarket.model.StockDetails
import com.stockmarket.repository.StockRepository
import com.stockmarket.utils.DateUtils
import com.stockmarket.utils.TimeRange
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StockViewModel(private val repository: StockRepository) : ViewModel() {

    private val _stocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> = _stocks

    private val _stockDetails = MutableStateFlow<StockDetails?>(null)
    val stockDetails: StateFlow<StockDetails?> = _stockDetails

    private val _historicalData = MutableStateFlow<HistoricalData?>(null)
    val historicalData: StateFlow<HistoricalData?> = _historicalData

    private var debounceJob: Job? = null

    fun searchStocksDebounced(query: String) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(700)
            searchStocks(query)
        }
    }

    private fun searchStocks(query: String) {
        viewModelScope.launch {
            try {
                Log.d("LOGS", "Searched for $query")
                _stocks.value = repository.searchStocks(query)
            } catch (e: Exception) {
                Log.e("ERROR", "searchStocks: ${e.message}")
            }
        }
    }

    fun fetchStockDetails(symbol: String) {
        viewModelScope.launch {
            try {
                _stockDetails.value = repository.getStockDetails(symbol)
            } catch (e: Exception) {
                Log.e("ERROR", "fetchStockDetails: ${e.message}")
            }
        }
    }

    fun fetchHistoricalData(symbol: String, from: String? = null, to: String? = null) {
        viewModelScope.launch {
            try {
                Log.d("LOGS", "Searched for historical data from:$from to $to")
                _historicalData.value = repository.getHistoricalData(symbol, from, to)
            } catch (e: Exception) {
                Log.e("ERROR", "fetchHistoricalData: ${e.message}")
            }
        }
    }

    fun fetchHistoricalDataTimeRanged(symbol: String, timeRange: TimeRange) {
        fetchHistoricalData(
            symbol = symbol,
            from = DateUtils.getDate(timeRange),
            to = DateUtils.getDate()
        )
    }
}