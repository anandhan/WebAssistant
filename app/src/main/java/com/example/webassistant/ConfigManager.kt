/**
 * ConfigManager.kt
 * 
 * Purpose:
 * This file defines the ConfigManager object, which is responsible for loading and managing configuration
 * properties from a 'config.properties' file stored in the app's assets folder. The configuration includes
 * settings such as the OpenAI API key and the model to be used for AI requests.
 * 
 * Key Components:
 * - initialize: Loads the 'config.properties' file from the assets folder and stores the properties.
 * - getOpenAIApiKey: Returns the OpenAI API key from the loaded properties.
 * - getOpenAIModel: Returns the OpenAI model to be used for AI requests, defaulting to 'gpt-4.1' if not specified.
 * 
 * The ConfigManager is used by other components (e.g., NetworkService) to access configuration settings
 * required for making API calls to external services.
 */

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