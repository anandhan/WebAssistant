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
            Log.e(TAG, "Failed to load config.properties", e)
            throw e
        }
    }

    fun getOpenAIApiKey(): String {
        val key = properties?.getProperty("OPENAI_API_KEY")
        if (key == null) {
            Log.e(TAG, "API key not found in config.properties")
            throw IllegalStateException("OpenAI API key not found in config.properties")
        }
        Log.d(TAG, "API key loaded successfully (length: ${key.length})")
        return key
    }
} 