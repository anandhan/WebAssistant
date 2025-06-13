/**
 * AIService.kt
 * 
 * Purpose:
 * This file defines the AIService interface, which is used by the NetworkService to make API calls to the
 * OpenAI API. It defines the structure of the request and response objects for processing questions.
 * 
 * Key Components:
 * - AIService: An interface that defines the API endpoint for processing questions using the OpenAI API.
 * - AIRequest: A data class that represents the request body, including the model, messages, and temperature.
 * - Message: A data class that represents a message in the conversation, with a role (e.g., 'system', 'user')
 *   and content.
 * - AIResponse: A data class that represents the response from the OpenAI API, containing a list of choices.
 * - Choice: A data class that represents a choice in the response, containing a message.
 * 
 * The AIService is used by the NetworkService to send requests to the OpenAI API and process the responses.
 */

package com.example.webassistant

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers

interface AIService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun processQuestion(
        @Body request: AIRequest
    ): AIResponse
}

data class AIRequest(
    val model: String = "gpt-4.1",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

data class AIResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
) 