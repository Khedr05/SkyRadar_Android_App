package com.example.skyradar

import androidx.recyclerview.widget.DiffUtil
import com.example.skyradar.model.Root

class MyDiffUtil : DiffUtil.ItemCallback<Root>() {
    override fun areItemsTheSame(oldItem: Root, newItem: Root): Boolean {
        return oldItem.cod == newItem.cod
    }

    override fun areContentsTheSame(oldItem: Root, newItem: Root): Boolean {
        return oldItem == newItem
    }
}