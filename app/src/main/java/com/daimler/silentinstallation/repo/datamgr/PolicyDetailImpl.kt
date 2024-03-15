package com.daimler.silentinstallation.repo.datamgr

import android.content.Context
import android.util.Log
import com.daimler.silentinstallation.network.ApiClient
import com.daimler.silentinstallation.repo.BluetoothPolicy
import com.daimler.silentinstallation.repo.WifiPolicy
import com.daimler.silentinstallation.utils.ActionUtils

import com.daimler.silentinstallation.utils.DeviceUtils
import com.daimler.silentinstallation.utils.LogUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PolicyDetailImpl(private val context: Context?) {

    private val apiClient by lazy { ApiClient }
    private val mainScope by lazy { MainScope() }

    private val wifiManager by lazy {
        WifiPolicy(context!!)
    }

    private val bluetoothManager by lazy {
        BluetoothPolicy(context!!)
    }


    /**
     *  appPackageDetail API service handler
     *  @return Void
     */

    fun policyDetail() {
        mainScope.launch {
            kotlin.runCatching {
                apiClient.policyDetail(DeviceUtils.DEVICE_ID)
            }
                .onSuccess {
                    try {
                        Log.d("TAG", "policyDetail: $it")
                        it.details.map { details ->
                            when (details.policyName) {
                                ActionUtils.POLICY_NAME_BLUETOOTH -> {
                                    bluetoothManager.applyBluetoothPolicy(details.policyStatus)
                                }

                                ActionUtils.POLICY_NAME_WIFI -> {
                                     wifiManager.applyWifiPolicy(details.policyStatus)
                                }
                                else -> {}
                            }
                        }
                    } catch (exception: Exception) {
                        Log.d(LogUtils.TAG, "exception: ${exception.message}")
                    }
                }
                .onFailure {
                    Log.d(LogUtils.TAG, "policyDetail onFailure : $it")
                }
        }
    }
}