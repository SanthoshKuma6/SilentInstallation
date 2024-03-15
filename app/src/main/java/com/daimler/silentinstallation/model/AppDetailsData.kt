package com.daimler.silentinstallation.model


import kotlinx.serialization.Serializable

@Serializable
data class AppDetailsData(
    val details: ArrayList<AppDetail>,
    val group_name: String,
    val group_token: String
) {
    @Serializable
    data class AppDetail(
        val app_name: String,
        val app_token: String,
        val package_name: String,
        val status: String,
        val apk_url: String,
        val app_image: String
    )
}
