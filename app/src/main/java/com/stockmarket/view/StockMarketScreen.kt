package com.stockmarket.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.stockmarket.model.HistoricalData
import com.stockmarket.model.Stock
import com.stockmarket.model.StockDetails
import com.stockmarket.utils.TimeRange
import com.stockmarket.viewmodel.StockViewModel

@Composable
fun StockMarketScreen(viewModel: StockViewModel) {
    val stocks by viewModel.stocks.collectAsState()
    val stockDetails by viewModel.stockDetails.collectAsState()
    val historicalData by viewModel.historicalData.collectAsState()
    var showList by remember { mutableStateOf(true) }


    Column(modifier = Modifier.padding(10.dp)) {
        var input by remember { mutableStateOf("") }
        TextField(
            value = input,
            readOnly = false,
            onValueChange = { query ->
                input = query
                viewModel.searchStocksDebounced(query)
            },
            label = { Text("Search for a stock") },
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showList = true
                        }
                    }
                }
                .padding(vertical = 32.dp)
        )

        if (showList) {
            LazyColumn(
                modifier = Modifier
                    .background(Color.LightGray)
            ) {
                items(stocks) { stock ->
                    StockItem(stock, onClick = {
                        showList = false
                        viewModel.fetchStockDetails(stock.symbol)
                        viewModel.fetchHistoricalData(stock.symbol)
                    })
                }
            }
        } else {
            StockDetails(stockDetails = stockDetails)
            StockGraphScreen(
                historicalData = historicalData,
                requestDataTimeRanged = { symbol, timeRange ->
                    viewModel.fetchHistoricalDataTimeRanged(symbol = symbol, timeRange = timeRange)
                }
            )
        }
    }
}

@Composable
fun StockItem(stock: Stock, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
            .background(Color.Gray)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(stock.longName ?: "", style = MaterialTheme.typography.titleMedium)
            Text(
                stock.symbol,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black.copy(0.6f)
            )
        }
    }
}

// Details
@Composable
fun StockDetails(stockDetails: StockDetails?) {

    stockDetails?.let { details ->
        Column(Modifier.padding(16.dp)) {
            Text(details.longName ?: "", style = MaterialTheme.typography.titleLarge)
            Text(details.symbol, style = MaterialTheme.typography.titleMedium)
            Text(
                "LTP: ${details.lastClosingPrice ?: ""}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "YTD Return: ${details.yearToDateReturn ?: ""}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    } ?: Text("Loading data...")
}

// Graph
@Composable
fun StockGraphScreen(
    historicalData: HistoricalData?,
    requestDataTimeRanged: (String, TimeRange) -> Unit
) {

    historicalData?.let { data ->
        Text(text = data.historical.firstOrNull()?.close?.toString() ?: "")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            StockGraph(data = data)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Button(onClick = { requestDataTimeRanged(data.symbol, TimeRange.ONE_WEEK) }) {
                    Text("1 Week")
                }
                Button(onClick = { requestDataTimeRanged(data.symbol, TimeRange.SIX_MONTHS) }) {
                    Text("6 Months")
                }
                Button(onClick = { requestDataTimeRanged(data.symbol, TimeRange.TWO_YEARS) }) {
                    Text("2 Years")
                }
            }
        }
    }
}

@Composable
fun StockGraph(data: HistoricalData) {
    if (data.historical.isNotEmpty()) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {

                    val entries = data.historical.mapIndexed { index, value ->
                        Entry(index.toFloat(), value.close.toFloat())
                    }

                    val dataSet = LineDataSet(entries, "Stock Prices").apply {
                        color = Color.Blue.hashCode()
                        valueTextColor = Color.Black.hashCode()
                        lineWidth = 2f
                    }

                    this.data = LineData(dataSet)
                    this.description.text = "Historical Data"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            update = { chart ->
                val entries = data.historical.mapIndexed { index, value ->
                    Entry(index.toFloat(), value.close.toFloat())
                }

                val dataSet = LineDataSet(entries, "Stock Prices").apply {
                    color = Color.Blue.hashCode()
                    valueTextColor = Color.Black.hashCode()
                    lineWidth = 2f
                }

                chart.data = LineData(dataSet)
                chart.invalidate()
            }
        )
        LaunchedEffect(data) {
            // when historicalData state is updated, this will call "update" part above, so the chart can be updated
        }
    }
}
