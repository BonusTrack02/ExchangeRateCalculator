package com.bonustrack02.exchangeratecalculator.network

import com.bonustrack02.exchangeratecalculator.dto.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateService {
    @GET("/api/live/")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String,
        @Query("currencies") currencies: String,
        @Query("source") source: String = "USD",
        @Query("format") format: Int = 1,
    ): Response<ExchangeRateResponse>
}