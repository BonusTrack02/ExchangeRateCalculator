package com.bonustrack02.exchangeratecalculator.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitObject {
    fun getInstance(baseUrl: String): Retrofit {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        clientBuilder.addInterceptor(loggingInterceptor)

        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        val retrofitBuilder = Retrofit.Builder().apply {
            baseUrl(baseUrl)
            addConverterFactory(ScalarsConverterFactory.create())
            addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            client(clientBuilder.build())
        }

        return retrofitBuilder.build()
    }
}