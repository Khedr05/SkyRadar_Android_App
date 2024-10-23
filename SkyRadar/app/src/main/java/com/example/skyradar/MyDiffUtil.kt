package com.example.skyradar

import androidx.recyclerview.widget.DiffUtil
import com.example.skyradar.model.ForecastResponse

class MyDiffUtil : DiffUtil.ItemCallback<ForecastResponse>() {
    override fun areItemsTheSame(oldItem: ForecastResponse, newItem: ForecastResponse): Boolean {
        return oldItem.cod == newItem.cod
    }

    override fun areContentsTheSame(oldItem: ForecastResponse, newItem: ForecastResponse): Boolean {
        return oldItem == newItem
    }
}