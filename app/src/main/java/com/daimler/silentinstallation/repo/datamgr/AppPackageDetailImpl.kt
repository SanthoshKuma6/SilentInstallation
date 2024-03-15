package com.daimler.silentinstallation.repo.datamgr

import android.content.Context
import android.util.Log
import com.daimler.silentinstallation.network.ApiClient
import com.daimler.silentinstallation.utils.ActionUtils
import com.daimler.silentinstallation.utils.DeviceUtils
import com.daimler.silentinstallation.utils.LogUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AppPackageDetailImpl(private val context: Context?) {

    private val apiClient by lazy { ApiClient }
    private val mainScope by lazy { MainScope() }

    private val silentInstallImpl by lazy {
        SilentInstallImpl(context!!)
    }

    /**
     *  appPackageDetail API service handler
     *  @return Void
     */

    fun appPackageDetail() {
        mainScope.launch {
            kotlin.runCatching {
                apiClient.appPackageDetail(DeviceUtils.DEVICE_ID)
            }
                .onSuccess {
                    try {
                        it.details.map { details ->
                            when (details.status) {
                                ActionUtils.ACTION_INSTALL -> {
                                    silentInstallImpl.downloadTask(
                                        fileUrl = details.apk_url,
                                        appName = details.app_name,
                                        packageName = details.package_name,
                                    )
                                }

                                ActionUtils.ACTION_UNINSTALL -> {
                                    silentInstallImpl.uninstallTask(
                                        packageName = details.package_name,
                                        appName = details.app_name
                                    )
                                }
                                else -> {}
                            }
                        }
                        } catch (exception: Exception) {
                            Log.d(LogUtils.TAG, "exception: ${exception.message}")
                        }
                }
                .onFailure {
                    Log.d(LogUtils.TAG, "appPackageDetail onFailure : $it")
                }
        }
    }
}