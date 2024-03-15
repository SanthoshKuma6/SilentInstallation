package com.daimler.silentinstallation.repo.datamgr

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.daimler.silentinstallation.network.ApiClient
import com.daimler.silentinstallation.repo.SilentPackageManager
import com.daimler.silentinstallation.utils.ActionUtils
import com.daimler.silentinstallation.utils.LogUtils
import kotlinx.coroutines.*
import java.io.FileInputStream

class SilentInstallImpl(private val context: Context) {

    private val silentPackageManager by lazy {
        SilentPackageManager(context)
    }

    /**
     * Download the file to be installed
     * @param fileUrl - file url to download the file
     * @param appName - application name to be downloaded
     * @param packageName - package name to be downloaded
     * @return Boolean
     */

    fun downloadTask(fileUrl: String, appName: String, packageName: String): Boolean {
        MainScope().launch {
            kotlin.runCatching {
                runBlocking {
                    fileUrl.let {
                        appName.let { it1 ->
                            ApiClient.download(
                                context, it,
                                it1
                            )
                        }
                    }
                }
            }
                .onSuccess {
                    prepareFile(appName, packageName)
                }

                .onFailure {
                    Log.d(LogUtils.TAG, LogUtils.file_not_downloaded)
                }

        }
        return true
    }

    /**
     *Silent Uninstallation of package
     * @param packageName - package details of the application to be uninstalled
     * @param appName - package details of the application to be uninstalled
     * @return Void
     */

    fun uninstallTask(packageName: String, appName :String): Boolean {
        silentPackageManager.uninstallPackage(packageName,appName)
        return true
    }

    /**
     * prepare the file to install silently after download
     * @param appName - application name to be installed
     * @param packageName - package name to be installed
     * @return Void
     */

    private suspend fun prepareFile(appName: String, packageName: String): Boolean {
        val fileInputStream: FileInputStream = context.openFileInput("$appName.apk")

        try {
            withContext(Dispatchers.Main) {
                packageName.let {
                    appName.let { it1 ->
                        SilentPackageManager(context)
                            .installPackage(
                            fileInputStream,
                            it, it1
                        )
                    }
                }
            }
        } catch (exception: Exception) {
            Log.d(LogUtils.TAG, exception.toString())
        }
        return true
    }
}