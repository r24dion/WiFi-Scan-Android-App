package com.example.lab1


data class ItemColumnModel(
    val title: String,
    val ssid: String,
    val bssid: String,
    val signalLevel: String,
    val freq: Int,
    val capabilities: String
)
