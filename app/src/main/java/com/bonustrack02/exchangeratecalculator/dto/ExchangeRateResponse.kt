package com.bonustrack02.exchangeratecalculator.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("terms")
    val terms: String,
    @SerialName("privacy")
    val privacy: String,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("source")
    val source: String,
    @SerialName("quotes")
    val quotes: Map<String, Double>
)