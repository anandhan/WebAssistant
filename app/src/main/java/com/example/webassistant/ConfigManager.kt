package com.example.webassistant

import android.content.Context
import android.util.Log
import java.util.Properties

object ConfigManager {
    private const val TAG = "ConfigManager"
    private var properties: Properties? = null

    fun initialize(context: Context) {
        try {
            if (properties == null) {
                Log.d(TAG, "Loading config.properties from assets")
                properties = Properties().apply {
                    context.assets.open("config.properties").use { input ->
                        load(input)
                    }
                }
                Log.d(TAG, "Config properties loaded successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading config.properties", e)
        }
    }

    fun getOpenAIApiKey(): String {
        return properties?.getProperty("OPENAI_API_KEY") ?: ""
    }

    fun getOpenAIModel(): String {
        return properties?.getProperty("OPENAI_MODEL", "gpt-4.1") ?: "gpt-4.1"
    }
} 