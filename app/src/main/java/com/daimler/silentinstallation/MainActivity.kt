package com.daimler.silentinstallation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.daimler.silentinstallation.databinding.ActivityMainBinding
import com.daimler.silentinstallation.repo.datamgr.AppPackageDetailImpl
import com.daimler.silentinstallation.service.ForegroundService
import com.daimler.silentinstallation.utils.DeviceUtils
import com.daimler.silentinstallation.utils.LogUtils

class MainActivity : AppCompatActivity() {

    private val mainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        if (hasBluetoothPermissions()) {
            Log.d("TAG", "onStartCommand: Granted")
        } else {
            // If permissions are not granted, request them
            Log.d("TAG", "onStartCommand: Notr Granted")
            requestBluetoothPermissions()

        }

        Intent(this, ForegroundService::class.java).also {
            this.startService(it)
        }

        DeviceUtils.DEVICE_ID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("BootCompleteService", "Device Id : ${DeviceUtils.DEVICE_ID}")



//        DeviceUtils.DEVICE_BUILD= Build.TYPE
//        Log.d(LogUtils.TAG, "DEVICE_BUILD : ${DeviceUtils.DEVICE_BUILD}")

    }

    private fun hasBluetoothPermissions(): Boolean {
        // Check if Bluetooth permissions are granted
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_ADMIN
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothPermissions() {
        // Request Bluetooth permissions from the user
        ActivityCompat.requestPermissions(
            this as Activity,
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN
            ),
            REQUEST_BLUETOOTH_PERMISSIONS
        )
    }
    companion object {
        const val REQUEST_BLUETOOTH_PERMISSIONS = 1
    }
}