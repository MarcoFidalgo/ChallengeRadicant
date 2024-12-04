package com.stockmarket.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar

object DateUtils {
    @SuppressLint("SimpleDateFormat")
    fun getDate(timeRange: TimeRange? = null): String {
        val calendar = Calendar.getInstance()

        timeRange?.let {
            when (it) {
                TimeRange.ONE_WEEK -> calendar.add(Calendar.DAY_OF_MONTH, -7)
                TimeRange.SIX_MONTHS -> calendar.add(Calendar.MONTH, -6)
                TimeRange.TWO_YEARS -> calendar.add(Calendar.YEAR, -2)
            }
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(calendar.time)
    }
}