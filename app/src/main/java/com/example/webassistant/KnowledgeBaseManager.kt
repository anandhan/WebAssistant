/**
 * KnowledgeBaseManager.kt
 * 
 * Purpose:
 * This file defines the KnowledgeBaseManager class, which is responsible for loading and managing a JSON-based
 * knowledge base stored in the app's assets folder. The knowledge base contains provider-specific information
 * (e.g., porting steps, common issues, Xfinity port-in guides) and general porting information.
 * 
 * Key Components:
 * - loadKnowledgeBase: Loads the JSON knowledge base from the assets folder during initialization.
 * - getProviderInfo: Retrieves information for a specific provider (e.g., AT&T, Verizon, T-Mobile).
 * - getPortingSteps: Returns a list of porting steps for a given provider.
 * - getCommonIssues: Returns a list of common issues and their solutions for a given provider.
 * - getXfinityPortInGuide: Returns a detailed guide for porting a number to Xfinity Mobile from a given provider.
 * - getGeneralPortingInfo: Returns general porting information, including common terms and best practices.
 * - searchKnowledgeBase: Performs a simple text-based search across the knowledge base for a given query.
 * 
 * The KnowledgeBaseManager is used by WebViewActivity to provide context-aware responses to user questions
 * based on the detected provider and the content of the knowledge base.
 */

package com.example.webassistant

import android.content.Context
import org.json.JSONObject
import java.io.IOException

class KnowledgeBaseManager(private val context: Context) {
    private var knowledgeBase: JSONObject? = null

    init {
        loadKnowledgeBase()
    }

    private fun loadKnowledgeBase() {
        try {
            val jsonString = context.assets.open("knowledge_base.json").bufferedReader().use { it.readText() }
            knowledgeBase = JSONObject(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getProviderInfo(provider: String): JSONObject? {
        return knowledgeBase?.getJSONObject("providers")?.optJSONObject(provider)
    }

    fun getPortingSteps(provider: String): List<Map<String, String>> {
        val steps = mutableListOf<Map<String, String>>()
        try {
            val providerInfo = getProviderInfo(provider)
            val portOutSteps = providerInfo?.getJSONArray("port_out_steps")
            
            for (i in 0 until (portOutSteps?.length() ?: 0)) {
                val step = portOutSteps?.getJSONObject(i)
                steps.add(mapOf(
                    "step" to (step?.getInt("step")?.toString() ?: ""),
                    "description" to (step?.getString("description") ?: ""),
                    "details" to (step?.getString("details") ?: "")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return steps
    }

    fun getCommonIssues(provider: String): List<Map<String, String>> {
        val issues = mutableListOf<Map<String, String>>()
        try {
            val providerInfo = getProviderInfo(provider)
            val commonIssues = providerInfo?.getJSONObject("porting_info")?.getJSONArray("common_issues")
            
            for (i in 0 until (commonIssues?.length() ?: 0)) {
                val issue = commonIssues?.getJSONObject(i)
                issues.add(mapOf(
                    "issue" to (issue?.getString("issue") ?: ""),
                    "solution" to (issue?.getString("solution") ?: ""),
                    "prevention" to (issue?.getString("prevention") ?: "")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return issues
    }

    fun getXfinityPortInGuide(provider: String): Map<String, Any> {
        val guide = mutableMapOf<String, Any>()
        try {
            val providerInfo = getProviderInfo(provider)
            val portInGuide = providerInfo?.getJSONObject("xfinity_port_in_guide")
            
            // Get prerequisites
            val prerequisites = mutableListOf<String>()
            val prereqArray = portInGuide?.getJSONArray("prerequisites")
            for (i in 0 until (prereqArray?.length() ?: 0)) {
                prerequisites.add(prereqArray?.getString(i) ?: "")
            }
            guide["prerequisites"] = prerequisites

            // Get steps
            val steps = mutableListOf<Map<String, String>>()
            val stepsArray = portInGuide?.getJSONArray("steps")
            for (i in 0 until (stepsArray?.length() ?: 0)) {
                val step = stepsArray?.getJSONObject(i)
                steps.add(mapOf(
                    "step" to (step?.getInt("step")?.toString() ?: ""),
                    "description" to (step?.getString("description") ?: ""),
                    "details" to (step?.getString("details") ?: "")
                ))
            }
            guide["steps"] = steps

            // Get other information
            guide["estimated_time"] = portInGuide?.getString("estimated_time") ?: ""
            
            val notes = mutableListOf<String>()
            val notesArray = portInGuide?.getJSONArray("important_notes")
            for (i in 0 until (notesArray?.length() ?: 0)) {
                notes.add(notesArray?.getString(i) ?: "")
            }
            guide["important_notes"] = notes

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return guide
    }

    fun getGeneralPortingInfo(): Map<String, Any> {
        val info = mutableMapOf<String, Any>()
        try {
            val generalInfo = knowledgeBase?.getJSONObject("general_porting_info")
            
            // Get common terms
            val terms = mutableMapOf<String, String>()
            val termsObj = generalInfo?.getJSONObject("common_terms")
            termsObj?.keys()?.forEach { key ->
                terms[key] = termsObj.getString(key)
            }
            info["common_terms"] = terms

            // Get best practices
            val practices = mutableListOf<String>()
            val practicesArray = generalInfo?.getJSONArray("best_practices")
            for (i in 0 until (practicesArray?.length() ?: 0)) {
                practices.add(practicesArray?.getString(i) ?: "")
            }
            info["best_practices"] = practices

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return info
    }

    fun searchKnowledgeBase(query: String): Map<String, Any> {
        val results = mutableMapOf<String, Any>()
        try {
            // Search in provider information
            val providers = knowledgeBase?.getJSONObject("providers")
            providers?.keys()?.forEach { provider ->
                val providerInfo = providers.getJSONObject(provider)
                if (providerInfo.toString().contains(query, ignoreCase = true)) {
                    results[provider] = providerInfo.toString()
                }
            }

            // Search in general information
            val generalInfo = knowledgeBase?.getJSONObject("general_porting_info")
            if (generalInfo?.toString()?.contains(query, ignoreCase = true) == true) {
                results["general_info"] = generalInfo.toString()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return results
    }
} 