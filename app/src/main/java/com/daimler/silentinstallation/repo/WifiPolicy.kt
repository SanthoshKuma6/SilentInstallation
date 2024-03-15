package com.daimler.silentinstallation.repo

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import com.daimler.silentinstallation.utils.ActionUtils
import com.daimler.silentinstallation.utils.LogUtils

class WifiPolicy(private val context: Context):BroadcastReceiver() {

    private val wifi = context.getSystemService(WIFI_SERVICE) as WifiManager
    private var wifiOn: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                val state = it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                updateWifiState(state)}
            }
        }


    private fun updateWifiState(state: Int) {
        when (state) {
            WifiManager.WIFI_STATE_ENABLED -> {
                Log.d(LogUtils.TAG, "WIFI_STATE_ENABLED")
                if (ActionUtils.WIFI_STATUS == ActionUtils.OFF){
                    wifi.isWifiEnabled = false
                }
            }
            WifiManager.WIFI_STATE_DISABLED -> {
                Log.d(LogUtils.TAG, "WIFI_STATE_DISABLED")
                if (ActionUtils.WIFI_STATUS == ActionUtils.ON) {
                    wifi.isWifiEnabled = true
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun applyWifiPolicy(policyStatus: String) {
        try {

            Log.d(LogUtils.TAG, "applyWifiPolicy: ")
            ActionUtils.WIFI_STATUS = policyStatus

            when (policyStatus) {

                ActionUtils.ON -> {
                    Log.d(LogUtils.TAG, "applyWifiPolicy: ON")
                    wifi.isWifiEnabled = true
                }

                ActionUtils.OFF -> {
                    Log.d(LogUtils.TAG, "applyWifiPolicy: OFF")
                    wifi.isWifiEnabled = false
                }

                else -> {
                    Log.d(LogUtils.TAG, "Invalid Wi-Fi policy value: $policyStatus")
                }

            }
        } catch (e: Exception) {
            Log.e(LogUtils.TAG, "Error applying Wi-Fi policy: ${e.message}")
        }
    }
}