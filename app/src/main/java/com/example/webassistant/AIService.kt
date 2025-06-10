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