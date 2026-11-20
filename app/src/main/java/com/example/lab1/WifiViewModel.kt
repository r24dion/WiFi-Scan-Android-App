package com.example.lab1

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WifiViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _wifiList = MutableStateFlow<List<ItemColumnModel>>(emptyList())
    val wifiList: StateFlow<List<ItemColumnModel>> = _wifiList

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            val success =
                intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
            if (success) {
                handleScanResults()
            }
        }
    }

    init {
        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(receiver, filter)
        startScan()
    }

    fun startScan() {
        if (hasLocationPermission()) {
            try {
                val started = wifiManager.startScan()
                Log.d("WIFI_TAG", "Scan started: $started")
            } catch (e: SecurityException) {
                Log.e("WIFI_TAG", "Permission denied for Wi-Fi scan", e)
            }
        } else {
            Log.w("WIFI_TAG", "Location permission not granted — cannot start scan")
        }
    }

    private fun handleScanResults() {
        if (!hasLocationPermission()) {
            Log.w("WIFI_TAG", "Permission not granted — cannot read scan results")
            return
        }

        try {
            val results = wifiManager.scanResults
            
            viewModelScope.launch {
                _wifiList.value = results.map { it.toItemModel() }
            }

            for (r in results) {
                Log.d("WIFI_TAG", "SSID: ${r.SSID}")
                Log.d("WIFI_TAG", "BSSID: ${r.BSSID}")
                Log.d("WIFI_TAG", "level: ${getSignalLevel(r.level)}")
                Log.d("WIFI_TAG", "frequency: ${r.frequency} MHz")
                Log.d("WIFI_TAG", "capabilities: ${r.capabilities}")
            }
        } catch (e: SecurityException) {
            Log.e("WIFI_TAG", "SecurityException reading scan results", e)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun ScanResult.toItemModel(): ItemColumnModel {
        return ItemColumnModel(
            title = SSID.ifEmpty { "<Hidden>" },
            ssid = SSID,
            bssid = BSSID,
            signalLevel = getSignalLevel(level),
            freq = frequency,
            capabilities = capabilities
        )
    }

    private fun getSignalLevel(level: Int): String = when {
        level > -50 -> "Excellent"
        level > -60 -> "Good"
        level > -70 -> "Fair"
        else -> "Weak"
    }

    override fun onCleared() {
        context.unregisterReceiver(receiver)
        super.onCleared()
    }
}
