package com.daimler.silentinstallation.repo

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInstaller
import android.util.Log
import com.daimler.silentinstallation.utils.IntentUtils
import com.daimler.silentinstallation.utils.LogUtils
import java.io.InputStream

class SilentPackageManager(private val context: Context) {

    /**
     * Install the package silently using package installer
     * @param apkStream - Path of the apk to be installed
     * @param packageName - Package name of the application to be installed
     * @param appName - Name of the application to be installed
     * @return Void
     */

    fun installPackage(
        apkStream: InputStream,
        packageName: String,
        appName: String,
    ) {
        val packageInstaller: PackageInstaller = context.packageManager.packageInstaller
        val params =
            PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        params.setAppPackageName(packageName)
        var session: PackageInstaller.Session? = null
        try {
            val sessionId = packageInstaller.createSession(params)
            session = packageInstaller.openSession(sessionId)
            val out = appName.let { session.openWrite(it, 0, -1) }
            val buffer = ByteArray(1024)
            var length: Int
            while (apkStream.read(buffer).also { length = it } != -1) {
                out.write(buffer, 0, length)
            }
            out.let { session.fsync(it) }
            out.close()
            createIntentSender(sessionId, context.packageName).let { intentSender ->
                session.commit(intentSender)
            }
            session.close()
            Log.d(LogUtils.TAG, "installPackage: $appName")
        } finally {
            session?.close()
        }
    }

    /**
     * Intent sender to install the package
     * @param sessionId - session id of installation
     * @param packageName - package name of the application to be installed silently
     * @return IntentSender
     */

    private fun createIntentSender(
        sessionId: Int,
        packageName: String?,
    ): IntentSender {
        val intent = Intent(IntentUtils.ACTION_INSTALL_COMPLETE)
        if (packageName != null) {
            intent.putExtra(IntentUtils.INTENT_EXTRA_PACKAGE_NAME, packageName)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, sessionId, intent, PendingIntent.FLAG_IMMUTABLE)
        return pendingIntent.intentSender
    }

    /**
     * Uninstall the package silently using package installer
     * @param data - package details of the application to be uninstalled
     * @param appName - Name of the application to be uninstalled
     * @return Void
     */

    fun uninstallPackage(data: String, appName: String) {
        try {
            val packageInstaller = context.packageManager.packageInstaller
            val params =
                PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            params.setAppPackageName(data)
            val sessionId: Int = packageInstaller.createSession(params)

            data.let {
                packageInstaller.uninstall(
                    it,
                    PendingIntent.getBroadcast(
                        context,
                        sessionId,
                        Intent(Intent.ACTION_MAIN),
                        PendingIntent.FLAG_IMMUTABLE
                    ).intentSender
                )
            }
            Log.d(LogUtils.TAG, "Application Name:  $appName uninstalled")
        } catch (exe: Exception) {
            Log.d(LogUtils.TAG, "uninstall exception : $exe")
        }
    }

}