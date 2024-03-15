package com.daimler.silentinstallation.repo

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import com.daimler.silentinstallation.utils.ActionUtils
import com.daimler.silentinstallation.utils.LogUtils


class BluetoothPolicy(private val context: Context) :BroadcastReceiver(){

    private val bluetoothAdapter = (context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter


    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                updateBluetoothState(state)
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun updateBluetoothState(state: Int) {
        when (state) {

            BluetoothAdapter.STATE_ON -> {
                Log.d(LogUtils.TAG, "STATE_ON: ")
                if (ActionUtils.BLUETOOTH_STATUS == ActionUtils.OFF){
                    bluetoothAdapter.disable()
                }
            }

            BluetoothAdapter.STATE_OFF -> {
                Log.d(LogUtils.TAG, "STATE_OFF: ")
                if (ActionUtils.BLUETOOTH_STATUS == ActionUtils.ON) {
                    bluetoothAdapter.enable()
                }
            }

        }
    }




    @SuppressLint("MissingPermission")
    fun applyBluetoothPolicy(policyStatus: String) {

        try {
            Log.d(LogUtils.TAG, "applyBluetoothPolicy: ")
            ActionUtils.BLUETOOTH_STATUS = policyStatus

            when (policyStatus) {
                ActionUtils.ON -> {
                    Log.d(LogUtils.TAG, "ON: ")
                    bluetoothAdapter.enable()
                }

                ActionUtils.OFF -> {
                    Log.d(LogUtils.TAG, "OFF: ")
                    bluetoothAdapter.disable()
                }

                else -> {
                    Log.d(LogUtils.TAG, "Invalid Bluetooth policy value: $policyStatus")
                }
            }
        } catch (e: Exception) {
            Log.d(LogUtils.TAG, "Error applying Bluetooth policy: ${e.message}")
        }
    }

}
