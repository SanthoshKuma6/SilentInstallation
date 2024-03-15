package com.daimler.silentinstallation.model

import kotlinx.serialization.Serializable


@Serializable
data class PolicyDetailData(
    val details: ArrayList<PolicyDetail>,
) {
    @Serializable
    data class PolicyDetail(
        val policyName: String,
        val policyStatus: String,
    )
}

