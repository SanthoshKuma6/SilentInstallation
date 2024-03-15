package com.daimler.silentinstallation.network

import android.annotation.SuppressLint
import android.content.Context
import com.daimler.silentinstallation.model.AppDetailsData
import com.daimler.silentinstallation.model.PolicyDetailData
import com.daimler.silentinstallation.utils.EndpointUtils
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object ApiClient {

    private val HttpClient by lazy {
        HttpClient()
    }



    /**
     * appPackageDetail function for get the particular applicationDetails
     * @param deviceId - DeviceId of the device to be registered
     * @return AppDetailsData - The response of the appPackageDetail
     */


    suspend fun appPackageDetail(deviceId: String?): AppDetailsData {
        val response = HttpClient.get("${EndpointUtils.DEVICE_APPLICATION_DETAIL}/$deviceId") {
            contentType(ContentType.Application.Json)
        }
        return Json.decodeFromString(response.bodyAsText())
    }

    /**
     * policyDetail function for get the particular applicationPolicyDetails
     * @param deviceId - DeviceId of the device to be registered
     * @return PolicyDetailsData - The response of the policyDetail
     */


    suspend fun policyDetail(deviceId: String?): PolicyDetailData {
        val response = HttpClient.get("${EndpointUtils.DEVICE_POLICY_DETAIL}/$deviceId") {
            contentType(ContentType.Application.Json)
        }
        return Json.decodeFromString(response.bodyAsText())
    }


    /**
     * Request to download the application
     * @param context - application context
     * @param url - url of the file to be downloaded and installed
     * @param appName - application name of the file to be downloaded and installed
     * @return Void
     */

    @SuppressLint("SuspiciousIndentation")
    suspend fun download(context: Context, url: String, appName: String) {
        val file = withContext(Dispatchers.IO) {
            File.createTempFile("files", "index")
        }
        runBlocking {
            url.let {
                HttpClient.prepareGet(it).execute { httpResponse ->
                    val channel: ByteReadChannel = httpResponse.body()
                    var bytesDownloaded = 0L
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.isEmpty) {
                            val bytes = packet.readBytes()
                            file.appendBytes(bytes)
                            bytesDownloaded += bytes.size.toLong()
                        }
                    }
                    val outputStream = context.openFileOutput("$appName.apk", Context.MODE_PRIVATE)
                    withContext(Dispatchers.IO) {
                        outputStream.write(file.readBytes())
                        outputStream.close()
                    }
                }
            }
        }
    }
}