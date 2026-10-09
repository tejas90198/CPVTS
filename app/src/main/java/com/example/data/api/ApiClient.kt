package com.example.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // Interceptor to dynamically redirect requests to whatever Base URL is currently configured
    private val dynamicBaseUrlInterceptor = Interceptor { chain ->
        var originalRequest = chain.request()
        val currentBase = ApiConfig.baseUrlState.value.toHttpUrlOrNull()

        if (currentBase != null) {
            val newUrl = originalRequest.url.newBuilder()
                .scheme(currentBase.scheme)
                .host(currentBase.host)
                .port(currentBase.port)
                .build()
            originalRequest = originalRequest.newBuilder().url(newUrl).build()
        }

        // Add auth token if available
        val token = ApiConfig.authTokenState.value
        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/json")
            .header("User-Agent", "CPVTS-Android-Client/1.0")

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(dynamicBaseUrlInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private var retrofitInstance: Retrofit = buildRetrofit()
    private var cachedBaseUrl: String = ApiConfig.DEFAULT_BASE_URL

    val service: CpvtsApiService by lazy {
        retrofitInstance.create(CpvtsApiService::class.java)
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.DEFAULT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Test connection to a given URL
     */
    suspend fun pingServer(targetUrl: String): Pair<Boolean, String> {
        return try {
            val cleanUrl = if (targetUrl.endsWith("/")) targetUrl else "$targetUrl/"
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url(cleanUrl)
                .get()
                .build()

            val response: Response = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                client.newCall(request).execute()
            }

            val code = response.code
            val isSuccess = code in 200..399
            Pair(isSuccess, "Server reached (HTTP $code: ${response.message})")
        } catch (e: Exception) {
            Pair(false, "Connection error: ${e.localizedMessage ?: e.javaClass.simpleName}")
        }
    }
}
