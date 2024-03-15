package com.daimler.silentinstallation.service

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.daimler.silentinstallation.utils.DeviceUtils
import com.daimler.silentinstallation.utils.LogUtils

/**
 *  Receiver to listen boot complete intent and start thr foreground service
 */

class BootCompleteService:BroadcastReceiver() {

    @SuppressLint("HardwareIds")
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
                Log.d(LogUtils.TAG,"BootCompleteService Received")

            Intent(context, ForegroundService::class.java).also {
                context.startService(it)
            }

            DeviceUtils.DEVICE_ID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            Log.d("BootCompleteService", "Device Id : ${DeviceUtils.DEVICE_ID}")


            DeviceUtils.DEVICE_NAME= getDeviceCategory(context)
            Log.d(LogUtils.TAG, "DEVICE_NAME : ${DeviceUtils.DEVICE_NAME}")

            DeviceUtils.DEVICE_BUILD= Build.TYPE
            Log.d(LogUtils.TAG, "DEVICE_BUILD : ${DeviceUtils.DEVICE_BUILD}")

        }
    }

    /**
     *  function to get the DeviceCategory.
     *  @param context - application context
     *  Return : Void
     */

    private fun getDeviceCategory(context: Context): String {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val configuration = context.resources.configuration

        // Check if the device is a TV
        if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            return "TV"
        }

        // Check if the device is a car
        if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_CAR) {
            return "Car"
        }

        // Check if the device is a tablet
        if (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return "Tablet"
        }

        // If it's not a TV, car, or tablet, assume it's a phone
        return "mobile device"
    }

}