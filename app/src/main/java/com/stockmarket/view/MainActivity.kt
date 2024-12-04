package com.stockmarket.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.stockmarket.model.api.FMPApiService
import com.stockmarket.repository.StockRepository
import com.stockmarket.view.ui.theme.StockMarketTheme
import com.stockmarket.viewmodel.StockViewModel
import com.stockmarket.viewmodel.StockViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {

    private val fmpApiService: FMPApiService by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://financialmodelingprep.com/api/v3/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FMPApiService::class.java)
    }

    private val stockRepository: StockRepository by lazy {
        StockRepository(fmpApiService)
    }

    private val stockViewModel: StockViewModel by lazy {
        ViewModelProvider(
            this,
            StockViewModelFactory(stockRepository)
        )[StockViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockMarketTheme {
                StockMarketScreen(stockViewModel)
            }
        }
    }
}
