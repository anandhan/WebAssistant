/**
 * NetworkService.kt
 * 
 * Purpose:
 * This file defines the NetworkService class, which is responsible for handling network requests to external
 * APIs, specifically the OpenAI API. It uses OkHttp for HTTP requests and Retrofit for API service definition.
 * 
 * Key Components:
 * - OkHttpClient: Configured with logging, timeouts, and an interceptor to add the OpenAI API key to requests.
 * - Retrofit: Used to create an instance of the AIService interface, which defines the API endpoints.
 * - AIService: An interface that defines the API call to process questions using the OpenAI API.
 * 
 * The NetworkService is used by other components (e.g., MainActivity, WebViewActivity) to send requests to the
 * OpenAI API and process the responses.
 */

package com.example.webassistant

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkService {
    private val TAG = "NetworkService"
    private val BASE_URL = "https://api.openai.com/"
    private val apiKey = ConfigManager.getOpenAIApiKey()
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            Log.d(TAG, "Making request to: ${request.url}")
            Log.d(TAG, "Request headers: ${request.headers}")
            Log.d(TAG, "Request body: ${request.body?.toString()}")
            try {
                val response = chain.proceed(request)
                Log.d(TAG, "Response code: ${response.code}")
                val responseBody = response.peekBody(Long.MAX_VALUE).string()
                Log.d(TAG, "Response body: $responseBody")
                if (!response.isSuccessful) {
                    Log.e(TAG, "Request failed with code ${response.code}")
                    Log.e(TAG, "Error response: $responseBody")
                }
                response
            } catch (e: Exception) {
                Log.e(TAG, "Network request failed", e)
                throw e
            }
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val aiService: AIService = retrofit.create(AIService::class.java)

    init {
        Log.d(TAG, "NetworkService initialized successfully")
        Log.d(TAG, "Using API key starting with: ${apiKey.take(10)}...")
    }
} 