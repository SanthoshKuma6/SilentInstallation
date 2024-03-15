package com.daimler.silentinstallation.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.daimler.silentinstallation.R
import com.daimler.silentinstallation.repo.BluetoothPolicy
import com.daimler.silentinstallation.repo.SilentPackageManager
import com.daimler.silentinstallation.repo.WifiPolicy
import com.daimler.silentinstallation.repo.datamgr.AppPackageDetailImpl
import com.daimler.silentinstallation.repo.datamgr.PolicyDetailImpl
import com.daimler.silentinstallation.utils.LogUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ForegroundService : Service() {

    private val context by lazy {this.applicationContext }
    private val wifiPolicy by lazy {
        WifiPolicy(context)
    }

    private val bluetoothPolicy by lazy {
        BluetoothPolicy(context)
    }

    /**
     * Start the foreground service
     * @param intent - Intent to start foreground service
     * @param flags - Flags to start foreground service
     * @param startId - Id to start foreground service
     * @return Int
     */

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceNotificationBuilder(context)

        Log.d(LogUtils.TAG,"ForegroundService Started")


//            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
//                GlobalScope.launch(Dispatchers.IO) {
//                    AppPackageDetailImpl(context).appPackageDetail()
//                }
//            }, 0, 60, TimeUnit.SECONDS)


        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
            GlobalScope.launch(Dispatchers.IO) {
                PolicyDetailImpl(context).policyDetail()
            }
        }, 0, 60, TimeUnit.SECONDS)

        registerWifiReceiver()
        registerBluetoothReceiver()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun registerBluetoothReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        }
        registerReceiver(bluetoothPolicy, filter)
        Log.d("TAG", "registerBluetoothReceiver: ")
    }


    private fun registerWifiReceiver() {
        IntentFilter().apply {
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            registerReceiver(wifiPolicy, this)
            Log.d("TAG", "registerWifiReceiver: ")
        }
    }

//    private fun hasBluetoothPermissions(): Boolean {
//        // Check if Bluetooth permissions are granted
//        return ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.BLUETOOTH
//        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.BLUETOOTH_CONNECT
//        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.BLUETOOTH_ADMIN
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestBluetoothPermissions() {
//        // Request Bluetooth permissions from the user
//        ActivityCompat.requestPermissions(
//            this as Activity,
//            arrayOf(
//                Manifest.permission.BLUETOOTH,
//                Manifest.permission.BLUETOOTH_CONNECT,
//                Manifest.permission.BLUETOOTH_ADMIN
//            ),
//            REQUEST_BLUETOOTH_PERMISSIONS
//        )
//    }
//    companion object {
//        const val REQUEST_BLUETOOTH_PERMISSIONS = 1
//    }




    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    /**
     * Notification builder for foreground service
     * @param context - application context
     * @return Void
     */

    private fun serviceNotificationBuilder(context: Context) {
        val channel = NotificationChannel(
            context.getString(R.string.channel),
            context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val remoteViews = RemoteViews(
            applicationContext.packageName,
            R.layout.custom
        )

        notificationManager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(
            this,
            context.getString(R.string.channel)
        )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContent(remoteViews)
            .setSmallIcon(android.R.color.transparent)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)

        startForeground(1001, notification.build())
    }
}