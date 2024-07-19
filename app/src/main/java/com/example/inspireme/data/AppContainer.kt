package com.example.inspireme.data

import android.content.Context
import com.example.inspireme.data.local.OfflineQuotationRepository
import com.example.inspireme.data.local.QuotationDatabase
import com.example.inspireme.data.local.QuotationRepository
import com.example.inspireme.data.network.NetworkQuotationsRepository
import com.example.inspireme.data.network.QuotableApiService
import com.example.inspireme.data.network.QuotationsRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val networkQuotationsRepository: QuotationsRepository
    val offlineQuotationRepository: QuotationRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    private val baseUrl = "https://api.quotable.io"

    private val json = Json{ ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(contentType))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: QuotableApiService by lazy {
        retrofit.create(QuotableApiService::class.java)
    }

    override val networkQuotationsRepository: QuotationsRepository by lazy {
        NetworkQuotationsRepository(retrofitService)
    }

    override val offlineQuotationRepository: QuotationRepository by lazy {
        OfflineQuotationRepository(
            quotationDao =  QuotationDatabase.getDatabase(context).quotationDao()
        )
    }
}